package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record TaskEditDto (
        long id,
        String taskName,
        String description,
        long parentId,

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime deadline,
        int estimatedHours,
        int actualHours,
        Status status
){
}
