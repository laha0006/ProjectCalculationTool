package dev.tolana.projectcalculationtool.dto;

import java.time.LocalDate;

public record CreateTaskFormDto(String taskName,
                                String taskDescription,
                                int estimatedHours,
                                LocalDate deadline) {
}
