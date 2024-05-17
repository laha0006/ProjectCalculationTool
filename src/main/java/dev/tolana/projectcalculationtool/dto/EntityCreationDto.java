package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.EntityType;

public record EntityCreationDto(
        String entityName,
        String description,
        long parentId,
        EntityType entityType
) {
}
