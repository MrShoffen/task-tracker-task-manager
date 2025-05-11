package org.mrshoffen.tasktracker.task.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrshoffen.tasktracker.commons.kafka.event.desk.DeskDeletedEvent;
import org.mrshoffen.tasktracker.commons.kafka.event.workspace.WorkspaceDeletedEvent;
import org.mrshoffen.tasktracker.task.service.TaskService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventListener {

    private final TaskService taskService;

    @KafkaListener(topics = WorkspaceDeletedEvent.TOPIC)
    public void handleWorkspaceDeletedEvent(WorkspaceDeletedEvent event) {
        log.info("Received event in topic {} - {}", WorkspaceDeletedEvent.TOPIC, event);
        taskService
                .deleteAllTasksInWorkspace(event.getWorkspaceId())
                .block();
    }

    @KafkaListener(topics = DeskDeletedEvent.TOPIC)
    public void handleDeskDeletedEvent(DeskDeletedEvent event) {
        log.info("Received event in topic {} - {}", DeskDeletedEvent.TOPIC, event);
        taskService
                .deleteAllTasksInDesk(event.getWorkspaceId(), event.getDeskId())
                .block();
    }


}
