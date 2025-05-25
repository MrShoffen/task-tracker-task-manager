package org.mrshoffen.tasktracker.task.repository;

import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TaskRepository extends ReactiveCrudRepository<Task, UUID> {

    @Query("SELECT COALESCE(MAX(t.order_index), 0) FROM tasks t WHERE t.desk_id = :deskId")
    Mono<Long> findMaxOrderIndexInDesk(@Param("deskId") UUID deskId);

    Flux<Task> findAllByWorkspaceId(UUID workspaceId);

    Flux<Task> findAllByWorkspaceIdAndDeskId(UUID workspaceId, UUID deskId);

    Mono<Void> deleteAllByWorkspaceId(UUID workspaceId);

    Mono<Void> deleteAllByWorkspaceIdAndDeskId(UUID workspaceId, UUID deskId);

    Mono<Task> findByWorkspaceIdAndId(UUID workspaceId, UUID taskId);

    Mono<Task> findByWorkspaceIdAndDeskIdAndId(UUID workspaceId, UUID deskId, UUID taskId);

}
