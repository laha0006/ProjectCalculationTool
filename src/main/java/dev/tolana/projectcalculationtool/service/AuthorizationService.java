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

    public AuthorizationService(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    private void initRoles() {
        roles = authorizationRepository.getRoles();
    }

    public boolean hasAccess(long id, Permission permission) {
        if (roles == null) {
            initRoles();
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HierarchyDto hierarchy = authorizationRepository.getHierarchy(id);
        List<Long> userRoleIds = authorizationRepository.getRoleIdsMatchingHierarchy(username, hierarchy);;

        return hasPermission(userRoleIds, permission);
    }

    private boolean hasPermission(List<Long> userRoleIds, Permission permission) {
        Set<Permission> permissions = new HashSet<>();
        for (Long roleId : userRoleIds) {
            permissions.addAll(roles.get(roleId).getPermissions());
        }
        return permissions.contains(permission);
    }

    public boolean canPerform(long id, String targetUser,Permission permission) {
        if (roles == null) {
            initRoles();
        }
        String userPerformingAction = SecurityContextHolder.getContext().getAuthentication().getName();
        HierarchyDto hierarchy = authorizationRepository.getHierarchy(id);
        List<Long> userPerformingActionRoleIds = authorizationRepository.getRoleIdsMatchingHierarchy(userPerformingAction, hierarchy);
        System.out.println("userPerformingAction: " + userPerformingActionRoleIds);
        List<Long> targetUserRoleIds = authorizationRepository.getRoleIdsMatchingHierarchy(targetUser, hierarchy);
        System.out.println("targetUserRoleIds: " + targetUserRoleIds);

        if (!hasPermission(userPerformingActionRoleIds,permission)) {
            return false;
        }
        return getHighestWeight(userPerformingActionRoleIds) > getHighestWeight(targetUserRoleIds);

    }

    private int getHighestWeight(List<Long> userRoleIds) {
        short highestWeight = 0;
        for (Long roleId : userRoleIds) {
            short weight = roles.get(roleId).getWeight();
            if (weight > highestWeight) {
                highestWeight = weight;
            }
        }
        System.out.println("Highest weight is " + highestWeight);
        return highestWeight;
    }
}
