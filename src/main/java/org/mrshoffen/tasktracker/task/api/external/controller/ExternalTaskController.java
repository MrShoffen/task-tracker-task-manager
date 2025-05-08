package org.mrshoffen.tasktracker.task.api.external.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.api.external.service.ExternalTaskService;
import org.mrshoffen.tasktracker.task.model.dto.TaskCreateDto;
import org.mrshoffen.tasktracker.task.model.dto.links.TaskDtoLinksInjector;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mrshoffen.tasktracker.commons.web.authentication.AuthenticationAttributes.AUTHORIZED_USER_HEADER_NAME;

/**
 * Эндпоинты доступные внешнему клиенту (через gateway)
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/workspaces/{workspaceId}/desks/{deskId}/tasks")
public class ExternalTaskController {

    private final TaskDtoLinksInjector linksInjector;

    private final ExternalTaskService taskService;

    @PostMapping
    Mono<ResponseEntity<TaskResponseDto>> createTask(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                     @Valid @RequestBody Mono<TaskCreateDto> taskCreateDto,
                                                     @PathVariable("workspaceId") UUID workspaceId,
                                                     @PathVariable("deskId") UUID deskId) {
        return taskCreateDto
                .flatMap(dto ->
                        taskService.createTaskOnUserDesk(dto, userId, workspaceId, deskId)
                )
                .map(linksInjector::injectLinks)
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
                .getAllTasksOnUsersDesk(userId, workspaceId, deskId)
                .map(linksInjector::injectLinks);
    }

    @DeleteMapping("/{taskId}")
    Mono<ResponseEntity<Void>> deleteUserTaskById(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                  @PathVariable("workspaceId") UUID workspaceId,
                                                  @PathVariable("deskId") UUID deskId,
                                                  @PathVariable("taskId") UUID taskId) {
        return taskService
                .deleteUserTaskById(userId, workspaceId, deskId, taskId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

}
