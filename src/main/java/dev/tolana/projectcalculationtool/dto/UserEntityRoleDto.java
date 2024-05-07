package dev.tolana.projectcalculationtool.dto;

public record UserEntityRoleDto(String username,
                                int roleId,
                                int taskId,
                                int projectId,
                                int teamId,
                                int departmentId,
                                int organizationId) {
}
