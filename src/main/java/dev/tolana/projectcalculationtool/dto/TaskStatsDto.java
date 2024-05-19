package dev.tolana.projectcalculationtool.dto;

public record TaskStatsDto(boolean isDone,
                           int estimatedHours,
                           int totalEstimatedHours,
                           int totalActualHours,
                           int subtaskCount,
                           int subtasksDone) {
}
