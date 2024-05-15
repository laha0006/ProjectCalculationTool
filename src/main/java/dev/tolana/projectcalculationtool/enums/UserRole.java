package dev.tolana.projectcalculationtool.enums;

public enum UserRole {
    ORGANISATION_OWNER(1),
    ORGANISATION_ADMIN(2),
    ORGANISATION_MEMBER(3),
    ORGANISATION_USER(4),
    DEPARTMENT_OWNER(5),
    DEPARTMENT_ADMIN(6),
    DEPARTMENT_MEMBER(7),
    DEPARTMENT_USER(8),
    TEAM_OWNER(9),
    TEAM_ADMIN(10),
    TEAM_MEMBER(11),
    TEAM_USER(12),
    PROJECT_OWNER(13),
    PROJECT_ADMIN(14),
    PROJECT_MEMBER(15),
    PROJECT_USER(16),
    TASK_OWNER(17),
    TASK_ADMIN(18),
    TASK_MEMBER(19),
    TASK_USER(20);

    private final long roleId;

    UserRole(long value) {
        roleId = value;
    }

    public long getRoleId() {
        return roleId;
    }

}
