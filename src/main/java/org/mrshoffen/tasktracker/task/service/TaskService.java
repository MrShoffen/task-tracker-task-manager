package org.mrshoffen.tasktracker.task.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.exception.EntityAlreadyExistsException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityNotFoundException;
import org.mrshoffen.tasktracker.task.event.TaskEventPublisher;
import org.mrshoffen.tasktracker.task.mapper.TaskMapper;
import org.mrshoffen.tasktracker.task.model.dto.OrderIndexUpdateDto;
import org.mrshoffen.tasktracker.task.model.dto.TaskCreateDto;
import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.mrshoffen.tasktracker.task.repository.TaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mrshoffen.tasktracker.commons.utils.OrderIndexGenerator.next;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final TaskEventPublisher eventPublisher;

    public Mono<TaskResponseDto> createTaskOnUserDesk(TaskCreateDto dto, UUID userId, UUID workspaceId, UUID deskId) {
        return taskRepository
                .findMaxOrderIndexInDesk(deskId)
                .flatMap(currentMaxOrderIndex -> {
                    Task task = taskMapper.toTask(dto, userId, workspaceId, deskId);
                    task.setOrderIndex(next(currentMaxOrderIndex));
                    return taskRepository.save(task);
                })
                .onErrorMap(DataIntegrityViolationException.class, e ->
                        new EntityAlreadyExistsException(
                                "Задача с именем '%s' уже существует на доске '%s'"
                                        .formatted(dto.name(), deskId)
                        )
                )
                .map(taskMapper::toTaskResponse);
    }

    public Flux<TaskResponseDto> getAllTasksOnUsersDesk(UUID workspaceId, UUID deskId) {
        return taskRepository
                .findAllByWorkspaceIdAndDeskId(workspaceId, deskId)
                .map(taskMapper::toTaskResponse);
    }

    public Mono<Void> deleteUserTaskById(UUID workspaceId, UUID taskId) {
        return taskRepository
                .findByWorkspaceIdAndId(workspaceId, taskId)
                .doOnSuccess(task -> {
                    if (task != null) {
                        eventPublisher.publishTaskDeletedEvent(task);
                    }
                })
                .switchIfEmpty(
                        Mono.error(new EntityNotFoundException(
                                "Задача с id %s не найдена в данном пространстве"
                                        .formatted(taskId.toString())
                        ))
                )
                .flatMap(taskRepository::delete);
    }

    public Mono<TaskResponseDto> updateTaskOrder(UUID workspaceId,
                                                 UUID taskId, OrderIndexUpdateDto updateDto) {
        return taskRepository
                .findByWorkspaceIdAndId(workspaceId,taskId)
                .switchIfEmpty(
                        Mono.error(new EntityNotFoundException(
                                "Задача с id %s не найдена в данном пространстве"
                                        .formatted(taskId.toString())
                        ))
                )
                .flatMap(task -> {
                    task.setOrderIndex(updateDto.updatedIndex());
                    return taskRepository.save(task);
                })
                .map(taskMapper::toTaskResponse);
    }

    public Mono<Void> deleteAllTasksInWorkspace(UUID workspaceId) {
        return taskRepository
                .deleteAllByWorkspaceId(workspaceId);
    }

    public Mono<Void> deleteAllTasksInDesk(UUID workspaceId, UUID deskId) {
        return taskRepository
                .deleteAllByWorkspaceIdAndDeskId(workspaceId, deskId);
    }


    public Flux<TaskResponseDto> getAllTasksInWorkspace(UUID workspaceId) {
        return taskRepository
                .findAllByWorkspaceId(workspaceId)
                .map(taskMapper::toTaskResponse);
    }

}
