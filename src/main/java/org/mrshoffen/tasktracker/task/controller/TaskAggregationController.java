package org.mrshoffen.tasktracker.task.controller;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Эндпоинты для агрегирующих сервисов.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/aggregate-api/workspaces")
public class TaskAggregationController {

    private final TaskService taskService;

    @GetMapping("/{workspaceId}/tasks")
    Flux<TaskResponseDto> getAllTasksInWorkspace(@PathVariable("workspaceId") UUID workspaceId) {
        return taskService
                .getAllTasksInWorkspace(workspaceId);
    }

    @GetMapping("/{workspaceId}/desks/{deskId}/tasks")
    Flux<TaskResponseDto> getAllTasksInDesk(@PathVariable("workspaceId") UUID workspaceId,
                                            @PathVariable("deskId") UUID deskId) {
        return taskService
                .getAllTasksOnUsersDesk(workspaceId, deskId);
    }

}
