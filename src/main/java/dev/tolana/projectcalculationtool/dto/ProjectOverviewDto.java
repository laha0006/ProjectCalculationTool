package dev.tolana.projectcalculationtool.dto;

import java.sql.Timestamp;

public record ProjectOverviewDto (
        String name,
        Timestamp deadline,
        int allottedHours,
        int status,
        long projectId) {
}
