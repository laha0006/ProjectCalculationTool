package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.enums.AccessLevel;
import dev.tolana.projectcalculationtool.enums.Permission;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.WeightedPermissionSetDto;
import dev.tolana.projectcalculationtool.model.Role;
import dev.tolana.projectcalculationtool.repository.AuthorizationRepository;
import org.springframework.security.core.Authentication;
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

    public boolean hasOrgansiationAccess(long organisationId, Permission requiredPermission) {
        return hasAccess(organisationId, AccessLevel.ORGANISATION, requiredPermission);
    }

    public boolean hasDepartmentAccess(long departmentId, Permission requiredPermission) {
        return hasAccess(departmentId, AccessLevel.DEPARTMENT, requiredPermission);
    }

    public boolean hasTeamAccess(long teamId, Permission requiredPermission) {
        return hasAccess(teamId, AccessLevel.TEAM, requiredPermission);
    }

    public boolean hasProjectAccess(long projectId, Permission requiredPermission) {
        System.out.println("PROJECT");
        return hasAccess(projectId, AccessLevel.PROJECT, requiredPermission);
    }

    public boolean hasTaskAccess(long taskId, Permission requiredPermission) {
        return hasAccess(taskId, AccessLevel.TASK, requiredPermission);
    }

    private void initRoles() {
        roles = authorizationRepository.getRoles();
    }

    private boolean hasAccess(long entityId, AccessLevel accessLevel, Permission requiredPermission) {
        if (roles == null) {
            initRoles();
        }
        //currently logged in user, gotten from session.
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        HierarchyDto hierarchy = authorizationRepository.getHierarchy(entityId, accessLevel);
        List<Long> userRoleIds = authorizationRepository.getRoleIdsMatchingHierarchy(username, hierarchy);
        ;

        return hasPermission(userRoleIds, requiredPermission);
    }

    private boolean hasPermission(List<Long> userRoleIds, Permission requiredPermission) {
        Set<Permission> permissions = new HashSet<>();
        for (Long roleId : userRoleIds) {
            permissions.addAll(roles.get(roleId).getPermissions());
        }
        return permissions.contains(requiredPermission); // O(1)
    }

//    public boolean canPerform(long id, String targetUser, Permission permission) {
//        if (roles == null) {
//            initRoles();
//        }
//        String userPerformingAction = SecurityContextHolder.getContext().getAuthentication().getName();
//        HierarchyDto hierarchy = authorizationRepository.getHierarchy(id);
//        List<Long> userPerformingActionRoleIds = authorizationRepository.getRoleIdsMatchingHierarchy(userPerformingAction, hierarchy);
//        System.out.println("userPerformingAction: " + userPerformingActionRoleIds);
//        List<Long> targetUserRoleIds = authorizationRepository.getRoleIdsMatchingHierarchy(targetUser, hierarchy);
//        System.out.println("targetUserRoleIds: " + targetUserRoleIds);
//
//        if (!hasPermission(userPerformingActionRoleIds, permission)) {
//            return false;
//        }
//        return getHighestWeight(userPerformingActionRoleIds) > getHighestWeight(targetUserRoleIds);
//
//    }

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
