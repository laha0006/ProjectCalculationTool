package dev.tolana.projectcalculationtool.dto;


import dev.tolana.projectcalculationtool.enums.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record TaskDto(
        String taskName,
        String taskDescription,
        long projectId,

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime deadline,
        int estimatedHours,
        Status status,
        long parentId,
        long taskId
) {
}
