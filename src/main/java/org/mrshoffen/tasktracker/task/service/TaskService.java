package org.mrshoffen.tasktracker.task.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.exception.EntityAlreadyExistsException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityNotFoundException;
import org.mrshoffen.tasktracker.task.event.TaskEventPublisher;
import org.mrshoffen.tasktracker.task.mapper.TaskMapper;
import org.mrshoffen.tasktracker.task.model.dto.edit.OrderIndexUpdateDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.TaskColorUpdateDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.TaskCompletionDto;
import org.mrshoffen.tasktracker.task.model.dto.create.TaskCreateDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.TaskCoverUpdateDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.TaskNameUpdateDto;
import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.mrshoffen.tasktracker.task.repository.TaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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

    public Mono<TaskResponseDto> updateTaskOrder(UUID workspaceId,
                                                 UUID taskId, OrderIndexUpdateDto updateDto) {
        return findTaskOrThrow(workspaceId, taskId)
                .flatMap(task -> {
                    task.setOrderIndex(updateDto.updatedIndex());
                    return taskRepository.save(task);
                })
                .map(taskMapper::toTaskResponse);
    }

    public Mono<TaskResponseDto> updateTaskCompletion(UUID workspaceId,
                                                      UUID taskId, TaskCompletionDto updateDto) {
        return findTaskOrThrow(workspaceId, taskId)
                .flatMap(task -> {
                    task.setCompleted(updateDto.getCompleted());
                    return taskRepository.save(task);
                })
                .map(taskMapper::toTaskResponse);
    }

    public Mono<TaskResponseDto> updateTaskName(UUID workspaceId, UUID taskId, TaskNameUpdateDto dto) {
        return findTaskOrThrow(workspaceId, taskId)
                .flatMap(task -> {
                    task.setName(dto.getNewName());
                    return taskRepository.save(task);
                })
                .onErrorMap(DuplicateKeyException.class, e ->
                        new EntityAlreadyExistsException(
                                "Задача с именем '%s' уже существует"
                                        .formatted(dto.getNewName())
                        )
                )
                .map(taskMapper::toTaskResponse);
    }

    public Mono<TaskResponseDto> updateTaskColor(UUID workspaceId, UUID taskId, TaskColorUpdateDto dto) {
        return findTaskOrThrow(workspaceId, taskId)
                .flatMap(task -> {
                    task.setColor(dto.newColor());
                    return taskRepository.save(task);
                })
                .map(taskMapper::toTaskResponse);
    }

    public Mono<TaskResponseDto> updateTaskCover(UUID workspaceId, UUID taskId, TaskCoverUpdateDto dto) {
        return findTaskOrThrow(workspaceId, taskId)
                .flatMap(task -> {
                    task.setCoverUrl(dto.newCoverUrl());
                    return taskRepository.save(task);
                })
                .map(taskMapper::toTaskResponse);
    }


    private Mono<Task> findTaskOrThrow(UUID workspaceId, UUID taskId) {
        return taskRepository
                .findByWorkspaceIdAndId(workspaceId, taskId)
                .switchIfEmpty(
                        Mono.error(new EntityNotFoundException(
                                "Задача с id %s не найдена в данном пространстве"
                                        .formatted(taskId.toString())
                        ))
                );
    }

}
