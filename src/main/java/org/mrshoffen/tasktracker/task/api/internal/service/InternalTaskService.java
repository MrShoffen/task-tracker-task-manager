package org.mrshoffen.tasktracker.task.api.internal.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalTaskService {

    private final TaskRepository taskRepository;

    public Mono<Void> deleteAllUserTasks(UUID userId, UUID workspaceId) {
        return taskRepository
                .deleteAllByUserIdAndWorkspaceId(userId,workspaceId);
    }

    public Mono<Void> deleteAllUserTasksInDesk(UUID userId, UUID workspaceId, UUID deskId) {
        return taskRepository
                .deleteAllByUserIdAndWorkspaceIdAndDeskId(userId,workspaceId,deskId);
    }
}
