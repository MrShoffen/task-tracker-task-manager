package org.mrshoffen.tasktracker.task.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.utils.HateoasLinks;
import org.mrshoffen.tasktracker.commons.web.dto.DeskResponseDto;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.model.dto.TaskCreateDto;
import org.mrshoffen.tasktracker.task.service.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mrshoffen.tasktracker.commons.web.authentication.AuthenticationAttributes.AUTHORIZED_USER_HEADER_NAME;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspaces/{workspaceId}/desks/{deskId}/tasks")
public class TaskController {

    @Value("${app.gateway.api-prefix}")
    private String apiPrefix;

    private final TaskService taskService;

    @PostMapping
    Mono<ResponseEntity<TaskResponseDto>> createTask(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                     @Valid @RequestBody Mono<TaskCreateDto> taskCreateDto,
                                                     @PathVariable("workspaceId") UUID workspaceId,
                                                     @PathVariable("deskId") UUID deskId) {
        return taskCreateDto
                .flatMap(dto ->
                        taskService.createTask(dto, userId, workspaceId, deskId)
                )
                .map(this::addLinks)
                .map(createdTask ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body(createdTask)
                );
    }

    @GetMapping
    Flux<TaskResponseDto> getAllTasksInDesk(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                            @PathVariable("workspaceId") UUID workspaceId,
                                            @PathVariable("deskId") UUID deskId) {
        return taskService
                .getAllTasksInDesk(userId, workspaceId, deskId)
                .map(this::addLinks);
    }


    public TaskResponseDto addLinks(TaskResponseDto dto) {
        HateoasLinks links = HateoasLinks.builder()
                .setPrefix(apiPrefix) // todo move to config server
                .addLink("allTasks",
                        "/workspaces/%s/desks/%s/tasks"
                                .formatted(dto.getWorkspaceId(), dto.getDeskId()),
                        "GET")
                .addLink("createTask",
                        "/workspaces/%s/desks/%s/tasks"
                                .formatted(dto.getWorkspaceId(), dto.getDeskId()),
                        "POST")
                .addLink("self",
                        "/workspaces/%s/desks/%s/tasks/%s"
                                .formatted(dto.getWorkspaceId(), dto.getDeskId(), dto.getId()),
                        "GET")
                .build();

        dto.setApi(links);
        return dto;
    }

}
