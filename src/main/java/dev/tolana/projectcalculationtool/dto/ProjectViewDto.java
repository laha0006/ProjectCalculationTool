package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Status;

import java.time.LocalDateTime;

public record ProjectViewDto(
        long id,
        long parentId,
        String projectName,
        String description,
        LocalDateTime deadline,
        Status status
) {
}
