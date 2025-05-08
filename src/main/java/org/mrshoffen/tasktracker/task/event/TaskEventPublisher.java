package org.mrshoffen.tasktracker.task.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.kafka.event.task.TaskDeletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventPublisher {

    private final KafkaTemplate<UUID, Object> kafkaTemplate;

    public Mono<Void> publishTaskDeletedEvent(UUID userId, UUID workspaceId, UUID deskId, UUID taskId) {
        TaskDeletedEvent event = new TaskDeletedEvent(userId, workspaceId, deskId, taskId);
        log.info("Event published to kafka topic '{}' - {}", TaskDeletedEvent.TOPIC, event);
        kafkaTemplate.send(TaskDeletedEvent.TOPIC, event.getTaskId(), event);
        return Mono.empty();
    }

}
