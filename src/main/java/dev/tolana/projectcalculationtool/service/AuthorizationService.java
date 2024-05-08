package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.constant.Permission;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.model.Role;
import dev.tolana.projectcalculationtool.repository.AuthorizationRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("auth")
public class AuthorizationService {

    private AuthorizationRepository authorizationRepository;
    private Map<Long, Role> roles;

    public AuthorizationService(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
        roles = initRoles();
    }

    private Map<Long, Role> initRoles() {
        return authorizationRepository.getRoles();
    }

    public boolean hasAccess(long id, AccessLevel accessLevel, Permission permission) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HierarchyDto hierarchy = getHierarchy(id, accessLevel);
        List<UserEntityRoleDto> userRoles = getRoleIdsMatchingHierarchy(username,hierarchy,accessLevel);
        return hasPermission(userRoles,permission);
    }

    private boolean hasPermission(List<UserEntityRoleDto> userRoles, Permission permission) {
        Set<Permission> permissions = new HashSet<>();
        for (UserEntityRoleDto role : userRoles) {
            permissions.addAll(roles.get(role.roleId()).getPermissions());
        }
        return permissions.contains(permission);
    }

    private boolean canDelete(String username, Set<Long> roleIds, Permission permission) {
        Set<Permission> permissions = new HashSet<>();
        for (Long roleId : roleIds) {
            permissions.addAll(roles.get(roleId).getPermissions());
        }
        return permissions.contains(permission);
    }

    private List<UserEntityRoleDto> getRoleIdsMatchingHierarchy(String username, HierarchyDto hierarchy, AccessLevel accessLevel) {
        List<UserEntityRoleDto> roleIds = authorizationRepository.getRoleIdsMatchingHierarchy(username,hierarchy,accessLevel);
    }

    private HierarchyDto getHierarchy(long id, AccessLevel accessLevel) {
        return authorizationRepository.getHierarchy(id,accessLevel);
    }
}
