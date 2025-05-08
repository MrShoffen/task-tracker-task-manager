package org.mrshoffen.tasktracker.task.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.kafka.event.desk.DeskDeletedEvent;
import org.mrshoffen.tasktracker.commons.kafka.event.workspace.WorkspaceDeletedEvent;
import org.mrshoffen.tasktracker.task.api.internal.service.InternalTaskService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventListener {

    private final InternalTaskService taskService;

    @KafkaListener(topics = WorkspaceDeletedEvent.TOPIC)
    public void handleDeskDeletedEvent(WorkspaceDeletedEvent event) {
        log.info("Received event in topic {} - {}", WorkspaceDeletedEvent.TOPIC, event);
        taskService
                .deleteAllUserTasks(event.getUserId(), event.getWorkspaceId())
                .block();
    }

    @KafkaListener(topics = DeskDeletedEvent.TOPIC)
    public void handleDeskDeletedEvent(DeskDeletedEvent event) {
        log.info("Received event in topic {} - {}", DeskDeletedEvent.TOPIC, event);
        taskService
                .deleteAllUserTasksInDesk(event.getUserId(), event.getWorkspaceId(), event.getDeskId())
                .block();
    }


}
