package org.mrshoffen.tasktracker.task.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.exception.TaskAlreadyExistsException;
import org.mrshoffen.tasktracker.task.model.dto.TaskCreateDto;
import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.mrshoffen.tasktracker.task.repository.TaskRepository;
import org.mrshoffen.tasktracker.task.util.client.DeskClient;
import org.mrshoffen.tasktracker.task.util.mapper.TaskMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final DeskClient deskClient;

    public Mono<TaskResponseDto> createTask(TaskCreateDto dto, UUID userId, UUID workspaceId, UUID deskId) {
        return deskClient
                .validateDeskStructure(userId, workspaceId, deskId)
                .then(
                        Mono.defer(() -> {
                            Task task = taskMapper.toTask(dto, userId, workspaceId, deskId);
                            return taskRepository.save(task);
                        })
                )
                .onErrorMap(DataIntegrityViolationException.class, e ->
                        new TaskAlreadyExistsException(
                                "Задача с именем '%s' уже существует на доске '%s'"
                                        .formatted(dto.name(), deskId)
                        )
                )
                .map(taskMapper::toTaskResponse);
    }

    public Flux<TaskResponseDto> getAllTasksInDesk(UUID userId, UUID workspaceId, UUID deskId) {
        return deskClient
                .validateDeskStructure(userId, workspaceId, deskId)
                .thenMany(
                        taskRepository
                                .findAllByUserIdAndWorkspaceIdAndDeskId(userId, workspaceId, deskId)
                )
                .map(taskMapper::toTaskResponse);
    }
}
