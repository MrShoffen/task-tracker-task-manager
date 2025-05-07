package org.mrshoffen.tasktracker.task.api.bff.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.mapper.TaskMapper;
import org.mrshoffen.tasktracker.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskAggregationService {

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    public Flux<TaskResponseDto> getAllTasksInWorkspace(UUID workspaceId) {
        return taskRepository
                .findAllByWorkspaceId(workspaceId)
                .map(taskMapper::toTaskResponse);
    }
}
