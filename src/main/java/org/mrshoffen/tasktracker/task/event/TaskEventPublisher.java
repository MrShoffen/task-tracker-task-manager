package org.mrshoffen.tasktracker.task.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.kafka.event.task.TaskCreatedEvent;
import org.mrshoffen.tasktracker.commons.kafka.event.task.TaskDeletedEvent;
import org.mrshoffen.tasktracker.commons.kafka.event.task.TaskUpdatedEvent;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventPublisher {

    private final KafkaTemplate<UUID, Object> kafkaTemplate;

    public void publishTaskDeletedEvent(Task task) {
        TaskDeletedEvent event = new TaskDeletedEvent(task.getUserId(), task.getWorkspaceId(), task.getDeskId(), task.getId(), Instant.now());
        log.info("Event published to kafka topic '{}' - {}", TaskDeletedEvent.TOPIC, event);
        kafkaTemplate.send(TaskDeletedEvent.TOPIC, event.getTaskId(), event);
    }

    public void publishTaskCreatedEvent(TaskResponseDto task) {
        TaskCreatedEvent ev = new TaskCreatedEvent(task);
        log.info("Event published to kafka topic '{}' - {}", TaskCreatedEvent.TOPIC, task);
        kafkaTemplate.send(TaskCreatedEvent.TOPIC, task.getId(), ev);
    }

    public void publishTaskUpdatedEvent(UUID workspaceId, UUID deskId, UUID taskId, String fieldName, Object newValue, UUID updatedBy) {
        TaskUpdatedEvent event = TaskUpdatedEvent.builder()
                .workspaceId(workspaceId)
                .deskId(deskId)
                .taskId(taskId)
                .updatedAt(Instant.now())
                .updatedBy(updatedBy)
                .updatedField(Map.of(fieldName, newValue))
                .build();
        log.info("Event published to kafka topic '{}' - {}", TaskUpdatedEvent.TOPIC, event);
        kafkaTemplate.send(TaskUpdatedEvent.TOPIC, event.getTaskId(), event);
    }
}
