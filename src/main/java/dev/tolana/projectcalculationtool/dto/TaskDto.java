package dev.tolana.projectcalculationtool.dto;


import java.time.LocalDateTime;

public record TaskDto(
        String taskName,
        String taskDescription,
        long projectId,
        LocalDateTime deadline,
        int estimatedHours,
        int status,
        long parentId,
        long taskId
) {
}
