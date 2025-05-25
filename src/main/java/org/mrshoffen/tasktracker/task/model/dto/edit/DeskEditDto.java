package org.mrshoffen.tasktracker.task.model.dto.edit;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeskEditDto(

        @NotNull(message = "Id доски не может быть пустым")
        UUID newDeskId
) {
}
