package org.mrshoffen.tasktracker.task.model.dto.edit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record TaskNameUpdateDto(
        @Size(max = 256, min = 1, message = "Имя доски должно быть между 1 и 256 символами")
        @NotBlank(message = "Имя доски не может быть пустым")
        String newName
) {
}
