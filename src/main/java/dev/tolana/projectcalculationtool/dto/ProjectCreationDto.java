package dev.tolana.projectcalculationtool.dto;

import java.time.LocalDateTime;

public record ProjectCreationDto(
        String projectName,
        String description,
        long parentId,
        long teamId,
        LocalDateTime deadline
) {
}
