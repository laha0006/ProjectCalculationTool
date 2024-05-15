package dev.tolana.projectcalculationtool.dto;


import dev.tolana.projectcalculationtool.enums.Status;

import java.time.LocalDateTime;

public record TaskDto(
        String taskName,
        String taskDescription,
        long projectId,
        LocalDateTime deadline,
        int estimatedHours,
        Status status,
        long parentId,
        long taskId
) {
}
