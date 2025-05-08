package org.mrshoffen.tasktracker.task.api.external.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.commons.web.exception.AccessDeniedException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityAlreadyExistsException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityNotFoundException;
import org.mrshoffen.tasktracker.task.client.DeskClient;
import org.mrshoffen.tasktracker.task.event.TaskEventPublisher;
import org.mrshoffen.tasktracker.task.mapper.TaskMapper;
import org.mrshoffen.tasktracker.task.model.dto.TaskCreateDto;
import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.mrshoffen.tasktracker.task.repository.TaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExternalTaskService {

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final DeskClient deskClient;

    private final TaskEventPublisher eventPublisher;

    public Mono<TaskResponseDto> createTaskOnUserDesk(TaskCreateDto dto, UUID userId, UUID workspaceId, UUID deskId) {
        return deskClient
                .ensureUserOwnsDesk(userId, workspaceId, deskId)
                .then(
                        Mono.defer(() -> {
                            Task task = taskMapper.toTask(dto, userId, workspaceId, deskId);
                            return taskRepository.save(task);
                        })
                )
                .onErrorMap(DataIntegrityViolationException.class, e ->
                        new EntityAlreadyExistsException(
                                "Задача с именем '%s' уже существует на доске '%s'"
                                        .formatted(dto.name(), deskId)
                        )
                )
                .map(taskMapper::toTaskResponse);
    }

    public Flux<TaskResponseDto> getAllTasksOnUsersDesk(UUID userId, UUID workspaceId, UUID deskId) {
        return deskClient
                .ensureUserOwnsDesk(userId, workspaceId, deskId)
                .thenMany(
                        taskRepository
                                .findAllByUserIdAndWorkspaceIdAndDeskId(userId, workspaceId, deskId)
                )
                .map(taskMapper::toTaskResponse);
    }

    public Mono<Void> deleteUserTaskById(UUID userId, UUID workspaceId, UUID deskId, UUID taskId) {
        return deskClient
                .ensureUserOwnsDesk(userId, workspaceId, deskId)
                .then(
                        Mono.defer(() ->
                                taskRepository.findByWorkspaceIdAndDeskIdAndId(workspaceId,deskId, taskId)
                        )
                )
                .switchIfEmpty(
                        Mono.error(new EntityNotFoundException(
                                "Задача с id '%s' не найдена".formatted(taskId)
                        ))
                )
                .flatMap(task -> {
                    if(task.getUserId().equals(userId)){
                        return eventPublisher
                                .publishTaskDeletedEvent(userId, workspaceId, deskId, taskId)
                                .then(taskRepository.delete(task));
                    } else {
                        return Mono.error(new AccessDeniedException(
                                "Пользователь не имеет доступа к данной задаче"
                        ));
                    }
                });
    }
}
