package org.mrshoffen.tasktracker.task.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.model.dto.create.TaskCreateDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.DeskEditDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.OrderIndexUpdateDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.TaskColorUpdateDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.TaskCompletionDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.TaskCoverUpdateDto;
import org.mrshoffen.tasktracker.task.model.dto.edit.TaskNameUpdateDto;
import org.mrshoffen.tasktracker.task.service.PermissionsService;
import org.mrshoffen.tasktracker.task.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import static org.mrshoffen.tasktracker.commons.web.permissions.Permission.*;

/**
 * Эндпоинты доступные внешнему клиенту (через gateway)
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/workspaces/{workspaceId}/desks/{deskId}/tasks")
public class ExternalTaskController {

    private final TaskService taskService;

    private final PermissionsService permissionsService;

    @PostMapping
    Mono<ResponseEntity<TaskResponseDto>> createTask(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                     @Valid @RequestBody Mono<TaskCreateDto> taskCreateDto,
                                                     @PathVariable("workspaceId") UUID workspaceId,
                                                     @PathVariable("deskId") UUID deskId) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, CREATE_TASK)
                .then(taskCreateDto.flatMap(dto ->
                        taskService.createTaskOnUserDesk(dto, userId, workspaceId, deskId))
                )
                .map(createdTask ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body(createdTask)
                );
    }

    @GetMapping
    Flux<TaskResponseDto> getAllTasksInDesk(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                            @PathVariable("workspaceId") UUID workspaceId,
                                            @PathVariable("deskId") UUID deskId) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, READ_WORKSPACE_CONTENT)
                .thenMany(taskService
                        .getAllTasksOnUsersDesk(workspaceId, deskId)
                );
    }

    @DeleteMapping("/{taskId}")
    Mono<ResponseEntity<Void>> deleteUserTaskById(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                                  @PathVariable("workspaceId") UUID workspaceId,
                                                  @PathVariable("deskId") UUID deskId,
                                                  @PathVariable("taskId") UUID taskId) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, DELETE_TASK)
                .then(taskService
                        .deleteUserTaskById(workspaceId, taskId)
                )
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PatchMapping("/{taskId}/order")
    Mono<TaskResponseDto> updateTaskOrder(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                          @PathVariable("workspaceId") UUID workspaceId,
                                          @PathVariable("deskId") UUID deskId,
                                          @PathVariable("taskId") UUID taskId,
                                          @Valid @RequestBody Mono<OrderIndexUpdateDto> updateDto) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, UPDATE_TASK_ORDER)
                .then(updateDto
                        .flatMap(dto ->
                                taskService.updateTaskOrder(workspaceId, taskId, dto, userId))
                );
    }

    @PatchMapping("/{taskId}/completion")
    Mono<TaskResponseDto> updateTaskCompletion(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                               @PathVariable("workspaceId") UUID workspaceId,
                                               @PathVariable("deskId") UUID deskId,
                                               @PathVariable("taskId") UUID taskId,
                                               @RequestBody Mono<TaskCompletionDto> updateDto) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, UPDATE_TASK_COMPLETION)
                .then(updateDto
                        .flatMap(dto ->
                                taskService.updateTaskCompletion(workspaceId, taskId, dto, userId))
                );
    }

    @PatchMapping("/{taskId}/name")
    Mono<TaskResponseDto> updateTaskName(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                         @PathVariable("workspaceId") UUID workspaceId,
                                         @PathVariable("deskId") UUID deskId,
                                         @PathVariable("taskId") UUID taskId,
                                         @Valid @RequestBody Mono<TaskNameUpdateDto> updateDto) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, UPDATE_TASK_NAME)
                .then(updateDto
                        .flatMap(dto ->
                                taskService.updateTaskName(workspaceId, taskId, dto, userId))
                );
    }

    @PatchMapping("/{taskId}/desk")
    Mono<TaskResponseDto> updateTaskDesk(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                         @PathVariable("workspaceId") UUID workspaceId,
                                         @PathVariable("deskId") UUID deskId,
                                         @PathVariable("taskId") UUID taskId,
                                         @Valid @RequestBody Mono<DeskEditDto> updateDto) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, UPDATE_TASK_DESK)
                .then(updateDto
                        .flatMap(dto ->
                                taskService.updateDesk(workspaceId, deskId, taskId, dto, userId))
                );
    }

    @PatchMapping("/{taskId}/color")
    Mono<TaskResponseDto> updateTaskColor(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                          @PathVariable("workspaceId") UUID workspaceId,
                                          @PathVariable("deskId") UUID deskId,
                                          @PathVariable("taskId") UUID taskId,
                                          @Valid @RequestBody Mono<TaskColorUpdateDto> updateDto) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, UPDATE_TASK_COLOR)
                .then(updateDto
                        .flatMap(dto ->
                                taskService.updateTaskColor(workspaceId, taskId, dto, userId))
                );
    }

    @PatchMapping("/{taskId}/cover")
    Mono<TaskResponseDto> updateTaskCover(@RequestHeader(AUTHORIZED_USER_HEADER_NAME) UUID userId,
                                          @PathVariable("workspaceId") UUID workspaceId,
                                          @PathVariable("deskId") UUID deskId,
                                          @PathVariable("taskId") UUID taskId,
                                          @Valid @RequestBody Mono<TaskCoverUpdateDto> updateDto) {
        return permissionsService
                .verifyUserPermission(userId, workspaceId, UPDATE_TASK_COVER)
                .then(updateDto
                        .flatMap(dto ->
                                taskService.updateTaskCover(workspaceId, taskId, dto, userId))
                );
    }

}
