package org.mrshoffen.tasktracker.task.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.kafka.event.task.TaskDeletedEvent;
import org.mrshoffen.tasktracker.task.model.entity.Task;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventPublisher {

    private final KafkaTemplate<UUID, Object> kafkaTemplate;

    public void publishTaskDeletedEvent(Task task) {
        TaskDeletedEvent event = new TaskDeletedEvent(task.getUserId(), task.getWorkspaceId(), task.getDeskId(), task.getId());
        log.info("Event published to kafka topic '{}' - {}", TaskDeletedEvent.TOPIC, event);
        kafkaTemplate.send(TaskDeletedEvent.TOPIC, event.getTaskId(), event);
    }

}
