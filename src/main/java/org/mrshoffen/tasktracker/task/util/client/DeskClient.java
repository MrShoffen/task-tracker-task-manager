package org.mrshoffen.tasktracker.task.util.client;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.task.exception.TaskStructureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mrshoffen.tasktracker.commons.web.authentication.AuthenticationAttributes.AUTHORIZED_USER_HEADER_NAME;

@RequiredArgsConstructor
public class DeskClient {

    private final WebClient webClient;

    public Mono<ResponseEntity<Void>> validateDeskStructure(UUID userId,
                                                            UUID workspaceId,
                                                            UUID deskId) {
        return webClient
                .get()
                .uri("/workspaces/{workspaceId}/desks/{deskId}", workspaceId, deskId)
                .header(AUTHORIZED_USER_HEADER_NAME, userId.toString())
                .retrieve()
                .toBodilessEntity()
                .onErrorMap(WebClientResponseException.NotFound.class, e ->
                        new TaskStructureException("Отсутствует запрошенная доска или пространство")
                );
    }

}
