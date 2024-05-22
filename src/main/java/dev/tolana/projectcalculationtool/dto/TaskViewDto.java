package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Status;

import java.time.LocalDateTime;

public record TaskViewDto(
        String taskName,
        String description,
        long id,
        LocalDateTime deadline,
        int estimatedHours,
        int actualHours,
        Status status
) {
}
