package org.mrshoffen.tasktracker.task.client;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.permissions.Permission;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
public class PermissionsClient {

    private final WebClient webClient;

    public Flux<Permission> getUserPermissionsForWorkspace(UUID userId, UUID workspaceId) {
        return webClient
                .get()
                .uri("/internal/permissions/users/{userId}/workspaces/{workspaceId}", userId, workspaceId)
                .retrieve()
                .bodyToFlux(Permission.class);
    }
}
