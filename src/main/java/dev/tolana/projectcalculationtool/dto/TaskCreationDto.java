package dev.tolana.projectcalculationtool.dto;

import java.time.LocalDateTime;

public record TaskCreationDto(
        String taskName,
        String description,
        long projectId,
        long parentId,
        LocalDateTime deadline,
        int estimatedHours
) {
}
