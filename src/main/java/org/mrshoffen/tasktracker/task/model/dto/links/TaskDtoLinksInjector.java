package org.mrshoffen.tasktracker.task.model.dto.links;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.tasktracker.commons.utils.link.Link;
import org.mrshoffen.tasktracker.commons.utils.link.Links;
import org.mrshoffen.tasktracker.commons.utils.link.LinksInjector;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;

@RequiredArgsConstructor
public class TaskDtoLinksInjector extends LinksInjector<TaskResponseDto> {

    private final String apiPrefix;

    @Override
    protected Links generateLinks(TaskResponseDto dto) {
        return Links.builder()
                .addLink(Link.forName("allTasks")
                        .andHref(apiPrefix + "/workspaces/%s/desks/%s/tasks"
                                .formatted(dto.getWorkspaceId(), dto.getDeskId()))
                        .andMethod("GET")
                        .build()
                )
                .addLink(Link.forName("createTask")
                        .andHref(apiPrefix + "/workspaces/%s/desks/%s/tasks"
                                .formatted(dto.getWorkspaceId(), dto.getDeskId()))
                        .andMethod("POST")
                        .build()
                )
                .addLink(Link.forName("self")
                        .andHref(apiPrefix + "/workspaces/%s/desks/%s/tasks/%s"
                                .formatted(dto.getWorkspaceId(), dto.getDeskId(), dto.getId()))
                        .andMethod("GET")
                        .build()
                )
                .build();

    }
}
