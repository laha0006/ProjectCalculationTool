package dev.tolana.projectcalculationtool.model;

import dev.tolana.projectcalculationtool.enums.Permission;

import java.util.HashSet;
import java.util.Set;

public class Role {
    private long id;
    private String roleName;
    private short weight;
    private Set<Permission> permissions;

    public Role() {}
    public Role(long id, String roleName, short weight) {
        this.id = id;
        this.roleName = roleName;
        this.weight = weight;
        this.permissions = new HashSet<>();
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public short getWeight() {
        return weight;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        return "Role{" +
               "id=" + id +
               ", roleName='" + roleName + '\'' +
               ", weight=" + weight +
               ", permissions=" + permissions +
               '}';
    }
}
