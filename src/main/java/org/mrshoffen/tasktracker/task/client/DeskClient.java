package org.mrshoffen.tasktracker.task.client;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.web.exception.AccessDeniedException;
import org.mrshoffen.tasktracker.commons.web.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class DeskClient {

    private final WebClient webClient;

    public Mono<ResponseEntity<Void>> ensureUserOwnsDesk(UUID userId,
                                                         UUID workspaceId,
                                                         UUID deskId) {
        return webClient
                .get()
                .uri("/internal/workspaces/{userId}/{workspaceId}/desks/{deskId}", userId, workspaceId, deskId)
                .retrieve()
                .toBodilessEntity()
                .onErrorMap(WebClientResponseException.NotFound.class, e ->
                        new EntityNotFoundException("Отсутствует запрошенная доска или пространство")
                )
                .onErrorMap(WebClientResponseException.Forbidden.class, e ->
                        new AccessDeniedException("Пользователь не имеет доступа к данному пространству '%s'"
                                .formatted(workspaceId))
                );
    }

}
