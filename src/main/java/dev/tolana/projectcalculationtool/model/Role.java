package dev.tolana.projectcalculationtool.model;

import dev.tolana.projectcalculationtool.enums.Permission;

import java.util.List;

public class Role {
    private long id;
    private String roleName;
    private short weight;
    private List<Permission> permissions;
}
