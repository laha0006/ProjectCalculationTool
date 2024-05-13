package dev.tolana.projectcalculationtool.dto;


import java.time.LocalDate;

public record TaskDto(
        String taskName,
        String taskDescription,
        long projectId,
        LocalDate deadline,
        int estimatedHours,
        int status,
        long parentId,
        long taskId
) {
}
