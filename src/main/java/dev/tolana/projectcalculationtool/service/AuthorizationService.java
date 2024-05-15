package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.enums.AccessLevel;
import dev.tolana.projectcalculationtool.enums.Permission;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.WeightedPermissionSetDto;
import dev.tolana.projectcalculationtool.model.Role;
import dev.tolana.projectcalculationtool.repository.AuthorizationRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("auth")
public class AuthorizationService {

    private AuthorizationRepository authorizationRepository;
    private Map<Long, Role> roles;
    private List<Permission> weigthedPermissions;
    private Map<UserRole,Long> stringLongMap;

    public AuthorizationService(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
        weigthedPermissions = new ArrayList<>(List.of(Permission.EDIT,Permission.DELETE));
    }

    private void init() {
        initRoles();
        initRoleNameToIdMap();
    }

    private void initRoles() {
        roles = authorizationRepository.getRoles();
    }

    private void initRoleNameToIdMap() {
        stringLongMap = new HashMap<>();
        for (Role role : roles.values()) {
            stringLongMap.put(UserRole.valueOf(role.getRoleName()), role.getId());
        }
    }

    public long roleId(UserRole userRole) {
        if (stringLongMap == null) {
            init();
        }
        return stringLongMap.get(userRole);
    }

    public boolean hasAccess(long id, AccessLevel accessLevel, Permission permission) {
        if (roles == null) {
            initRoles();
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HierarchyDto hierarchy = getHierarchy(id, accessLevel);
        List<UserEntityRoleDto> userRoles = getRoleIdsMatchingHierarchy(username, hierarchy, accessLevel);

        if (weigthedPermissions.contains(permission)) {
            return hasWeightedPermission(userRoles,accessLevel,permission);
        } else {
            return hasPermission(userRoles, permission);
        }
    }

    private boolean hasPermission(List<UserEntityRoleDto> userRoles, Permission permission) {
        Set<Permission> permissions = new HashSet<>();
        for (UserEntityRoleDto role : userRoles) {
            permissions.addAll(roles.get(role.roleId()).getPermissions());
        }
        return permissions.contains(permission);
    }

    private boolean hasWeightedPermission(List<UserEntityRoleDto> userRoles, AccessLevel accessLevel, Permission permission) {
        Map<AccessLevel, List<WeightedPermissionSetDto>> permissions = getWeigthedPermissionMap(userRoles,accessLevel);
        boolean hasWeightedPermission = false;
        for (var entry : permissions.entrySet()) {
            for (WeightedPermissionSetDto weightedPermissionSetDto : entry.getValue()) {
                if (entry.getKey().equals(accessLevel)) {;
                    hasWeightedPermission = (weightedPermissionSetDto.permissions().contains(permission) && weightedPermissionSetDto.weight() == 255);
                } else {
                    hasWeightedPermission = weightedPermissionSetDto.permissions().contains(permission);
                }
            }
        }
        return hasWeightedPermission;
    }

    private Map<AccessLevel, List<WeightedPermissionSetDto>> getWeigthedPermissionMap(List<UserEntityRoleDto> userRoles, AccessLevel accessLevel) {
        Map<AccessLevel, List<WeightedPermissionSetDto>> permissions = new HashMap<>();
        for (UserEntityRoleDto role : userRoles) {
            short weight = roles.get(role.roleId()).getWeight();
            Set<Permission> userRolePermissions = roles.get(role.roleId()).getPermissions();
            if (role.organizationId() > 0) {
                if (!permissions.containsKey(AccessLevel.ORGANISATION)) {
                    permissions.put(AccessLevel.ORGANISATION, new ArrayList<>());
                }
                permissions.get(AccessLevel.ORGANISATION).add(new WeightedPermissionSetDto(weight, userRolePermissions));
            }
            if (role.departmentId() > 0) {
                if (!permissions.containsKey(AccessLevel.DEPARTMENT)) {
                    permissions.put(AccessLevel.DEPARTMENT, new ArrayList<>());
                }
                permissions.get(AccessLevel.DEPARTMENT).add(new WeightedPermissionSetDto(weight, userRolePermissions) );
            }
            if (role.teamId() > 0) {
                if (!permissions.containsKey(AccessLevel.TASK)) {
                    permissions.put(AccessLevel.TEAM, new ArrayList<>());
                }
                permissions.get(AccessLevel.TEAM).add(new WeightedPermissionSetDto(weight, userRolePermissions) );
            }
            if (role.projectId() > 0) {
                if (!permissions.containsKey(AccessLevel.PROJECT)) {
                    permissions.put(AccessLevel.PROJECT, new ArrayList<>());
                }
                permissions.get(AccessLevel.PROJECT).add(new WeightedPermissionSetDto(weight, userRolePermissions) );
            }
            if (role.taskId() > 0) {
                if (!permissions.containsKey(AccessLevel.TASK)) {
                    permissions.put(AccessLevel.TASK, new ArrayList<>());
                }
                permissions.get(AccessLevel.TASK).add(new WeightedPermissionSetDto(weight, userRolePermissions) );
            }
        }
        return permissions;
    }

    private List<UserEntityRoleDto> getRoleIdsMatchingHierarchy(String username, HierarchyDto hierarchy, AccessLevel accessLevel) {
        return authorizationRepository.getRoleIdsMatchingHierarchy(username, hierarchy, accessLevel);
    }

    private HierarchyDto getHierarchy(long id, AccessLevel accessLevel) {
        return authorizationRepository.getHierarchy(id, accessLevel);
    }
}
