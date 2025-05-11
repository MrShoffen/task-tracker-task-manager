package org.mrshoffen.tasktracker.task.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.exception.AccessDeniedException;
import org.mrshoffen.tasktracker.commons.web.permissions.Permission;
import org.mrshoffen.tasktracker.task.client.PermissionsClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionsService {

    private final PermissionsClient permissionsClient;

    public Mono<Void> verifyUserPermission(UUID userId, UUID workspaceId, Permission permissionToVerify) {
        return permissionsClient
                .getUserPermissionsForWorkspace(userId, workspaceId)
                .filter(permission -> permission.equals(permissionToVerify))
                .switchIfEmpty(
                        Mono.error(
                                new AccessDeniedException(
                                        "У пользователя нет необходимых прав в данном пространстве: %s"
                                                .formatted(permissionToVerify.name())
                                ))
                )
                .then();
    }
}
