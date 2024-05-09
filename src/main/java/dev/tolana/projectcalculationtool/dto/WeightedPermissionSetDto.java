package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Permission;

import java.util.Set;

public record WeightedPermissionSetDto(Short weight, Set<Permission> permissions) {
}
