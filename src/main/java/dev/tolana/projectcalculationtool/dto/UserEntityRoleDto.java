package dev.tolana.projectcalculationtool.dto;

public record UserEntityRoleDto(String username,
                                long roleId,
                                long taskId,
                                long projectId,
                                long teamId,
                                long departmentId,
                                long organizationId) {
}


