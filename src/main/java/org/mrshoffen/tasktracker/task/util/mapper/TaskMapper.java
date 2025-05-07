package org.mrshoffen.tasktracker.task.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mrshoffen.tasktracker.commons.web.dto.TaskResponseDto;
import org.mrshoffen.tasktracker.task.model.dto.TaskCreateDto;
import org.mrshoffen.tasktracker.task.model.entity.Task;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    Task toTask(TaskCreateDto taskCreateDto, UUID userId, UUID workspaceId, UUID deskId);

    TaskResponseDto toTaskResponse(Task task);
}
