package dev.tolana.projectcalculationtool.dto;

public record EntityViewDto(
        String name,
        String description,
        long id,
        long parentId,
        boolean isArchived
) {
}
