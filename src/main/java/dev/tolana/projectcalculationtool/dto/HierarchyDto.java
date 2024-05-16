package dev.tolana.projectcalculationtool.dto;

public record HierarchyDto(long taskId,
                           long projectId,
                           long teamId,
                           long departmentId,
                           long organizationId,
                           long parentProjectId,
                           long parentTaskId) {
}
