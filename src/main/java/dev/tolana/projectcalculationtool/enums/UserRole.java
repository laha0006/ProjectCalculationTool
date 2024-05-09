package dev.tolana.projectcalculationtool.enums;

public enum UserRole {
    OWNER(1),
    ADMIN(2),
    MEMBER(3),
    USER(4);

    private final long roleId;

    UserRole(long value) {
        roleId = value;
    }
    public long getRoleId() {
        return roleId;
    }

}
