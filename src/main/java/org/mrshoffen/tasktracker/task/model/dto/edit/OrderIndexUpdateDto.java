package org.mrshoffen.tasktracker.task.model.dto.edit;

import jakarta.validation.constraints.NotNull;

public record OrderIndexUpdateDto(
        @NotNull(message = "Новый индекс не может быть пустым")
        Long updatedIndex
) {
}
