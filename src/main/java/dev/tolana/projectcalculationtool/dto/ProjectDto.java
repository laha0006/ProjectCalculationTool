package dev.tolana.projectcalculationtool.dto;

import java.time.LocalDate;

public record ProjectDto(
        int projectId,
        int projectParentId,
        int teamId,
        String projectName,
        String projectDescription,
        LocalDate projectDeadline,
        int status //TODO MAKE ENUM
) {
}
