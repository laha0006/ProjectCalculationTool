package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Status;

import java.sql.Timestamp;

public record ProjectOverviewDto (
        String name,
        Timestamp deadline,
        int allottedHours,
        Status status,
        long projectId) {
}
