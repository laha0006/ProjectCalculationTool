package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ResourceEntityViewDto(
        String resourceEntityName,
        String description,
        long id,
        long parentId,
        long teamId,
        long projectId,

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime deadline,
        int estimatedHours,
        int actualHours,
        int allottedHours,
        Status status
) {
}
