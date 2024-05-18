package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.EntityType;

import java.time.LocalDateTime;

public record ResourceEntityCreationDto(
        String resourceEntityName,
        String description,
        int estimatedHours,
        LocalDateTime deadline,
        long parentId,
        long teamId,
        long projectId,
        EntityType entityType
) {
}
