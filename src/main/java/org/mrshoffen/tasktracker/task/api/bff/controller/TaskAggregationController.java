package org.mrshoffen.tasktracker.task.api.bff.controller;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.api.bff.service.TaskAggregationService;
import org.mrshoffen.tasktracker.task.model.dto.links.TaskDtoLinksInjector;
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

    private final TaskDtoLinksInjector linksInjector;

    private final TaskAggregationService taskService;

    @GetMapping("/{workspaceId}/tasks/full")
    Flux<TaskResponseDto> getAllTasksInWorkspace(@PathVariable("workspaceId") UUID workspaceId) {
        return taskService
                .getAllTasksInWorkspace(workspaceId)
                .map(linksInjector::injectLinks);
    }

}
