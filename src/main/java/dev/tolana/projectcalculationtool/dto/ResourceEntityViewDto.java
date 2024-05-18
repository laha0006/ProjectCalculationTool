package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Status;

import java.time.LocalDateTime;

public record ResourceEntityViewDto(
        String resourceEntityName,
        String description,
        long id,
        long parentId,
        LocalDateTime deadline,
        int estimatedHours,
        int actualHours,
        int allottedHours,
        Status status
) {
}
