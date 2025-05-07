package org.mrshoffen.tasktracker.task.repository;

import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TaskRepository extends ReactiveCrudRepository<Task, UUID> {

    Flux<Task> findAllByUserIdAndWorkspaceIdAndDeskId(UUID userId, UUID workspaceId, UUID deskId);

}
