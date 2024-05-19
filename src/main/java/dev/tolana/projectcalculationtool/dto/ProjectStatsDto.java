package dev.tolana.projectcalculationtool.dto;

import java.util.List;
import java.util.Map;

public record ProjectStatsDto(int allottedHours,
                              int totalEstimatedHours,
                              int totalActualHours,
                              int totalTaskCount,
                              int tasksDone,
                              double actualOverAllottedHours,
                              double actualHoursOverEstimatedHours,
                              double estimatedOverAllottedHours,
                              Map<Long,TaskStatsDto> tasksMap,
                              Map<Long,ProjectStatsDto> subProjects) {
}
