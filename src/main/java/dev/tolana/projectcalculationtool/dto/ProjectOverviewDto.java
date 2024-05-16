package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Status;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public record ProjectOverviewDto (
        String name,
        LocalDateTime deadline,
        int allottedHours,
        Status status,
        long projectId) {
}
