package dev.tolana.projectcalculationtool.dto;

public record EntityCreationDto(
        String entityName,
        String description,
        long entityId
) {
}
