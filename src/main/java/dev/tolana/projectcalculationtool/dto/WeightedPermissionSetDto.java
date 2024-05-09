package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.constant.Permission;

import java.util.Set;

public record WeightedPermissionSetDto(Short weight, Set<Permission> permissions) {
}
