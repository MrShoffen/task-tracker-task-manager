package org.mrshoffen.tasktracker.task.repository;

import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TaskRepository extends ReactiveCrudRepository<Task, UUID> {

    Flux<Task> findAllByUserIdAndWorkspaceIdAndDeskId(UUID userId, UUID workspaceId, UUID deskId);

    Flux<Task> findAllByWorkspaceId(UUID workspaceId);

    Flux<Task> findAllByWorkspaceIdAndDeskId(UUID workspaceId, UUID deskId);

    Mono<Void> deleteAllByUserIdAndWorkspaceId(UUID userId, UUID workspaceId);

    Mono<Void> deleteAllByUserIdAndWorkspaceIdAndDeskId(UUID userId, UUID workspaceId, UUID deskId);

    Mono<Task> findByWorkspaceIdAndDeskIdAndId(UUID workspaceId, UUID deskId, UUID taskId);
}
