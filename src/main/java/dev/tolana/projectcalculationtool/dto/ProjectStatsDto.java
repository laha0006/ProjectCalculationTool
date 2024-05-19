package dev.tolana.projectcalculationtool.dto;

public record ProjectStatsDto(int totalEstimatedHours,
                              int totalActualHours,
                              int tasksDone) {
}
