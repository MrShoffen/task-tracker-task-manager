package org.mrshoffen.tasktracker.task.model.dto.edit;

import jakarta.validation.constraints.NotNull;

public record TaskColorUpdateDto(
        String newColor
) {
}
