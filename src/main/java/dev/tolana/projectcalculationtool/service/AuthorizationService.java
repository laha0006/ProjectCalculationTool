package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.enums.Permission;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
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

    public boolean hasOrgansiationAccess(long organisationId, Permission requiredPermission) {
        return hasAccess(organisationId, EntityType.ORGANISATION, requiredPermission);
    }

    public boolean hasDepartmentAccess(long departmentId, Permission requiredPermission) {
        return hasAccess(departmentId, EntityType.DEPARTMENT, requiredPermission);
    }

    public boolean hasTeamAccess(long teamId, Permission requiredPermission) {
        return hasAccess(teamId, EntityType.TEAM, requiredPermission);
    }

    public boolean hasProjectAccess(long projectId, Permission requiredPermission) {

        return hasAccess(projectId, EntityType.PROJECT, requiredPermission);
    }

    public boolean hasTaskAccess(long taskId, Permission requiredPermission) {
        return hasAccess(taskId, EntityType.TASK, requiredPermission);
    }

    private void initRoles() {
        roles = authorizationRepository.getRoles();
    }

    private boolean hasAccess(long entityId, EntityType entityType, Permission requiredPermission) {
        if (roles == null) {
            initRoles();
        }
        //currently logged in user, gotten from session.
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        HierarchyDto hierarchy = authorizationRepository.getHierarchy(entityId, entityType);
        if (hierarchy == null) {
            return false;
        }
        List<Long> userRoleIds = authorizationRepository.getRoleIdsMatchingHierarchy(username, hierarchy);

        return hasPermission(userRoleIds, requiredPermission);
    }

    private boolean hasPermission(List<Long> userRoleIds, Permission requiredPermission) {
        Set<Permission> permissions = new HashSet<>();
        for (Long roleId : userRoleIds) {
            permissions.addAll(roles.get(roleId).getPermissions());
        }
        return permissions.contains(requiredPermission); // O(1)
    }



    private int getHighestWeight(List<Long> userRoleIds) {
        short highestWeight = 0;
        for (Long roleId : userRoleIds) {
            short weight = roles.get(roleId).getWeight();
            if (weight > highestWeight) {
                highestWeight = weight;
            }
        }

        return highestWeight;
    }
}
