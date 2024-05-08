package dev.tolana.projectcalculationtool.dto;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public record TaskDto(
        String taskName,
        String taskDescription,
        long projectId,
        LocalDate deadline,
        int estimatedHours,
        int status,
        long parentId
) {
}
