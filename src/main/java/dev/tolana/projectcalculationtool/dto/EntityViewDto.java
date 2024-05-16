package dev.tolana.projectcalculationtool.dto;

public record EntityViewDto(
        String entityName,
        String description,
        long entityId,
        long foreignId,
        boolean isArchived
) {
}
