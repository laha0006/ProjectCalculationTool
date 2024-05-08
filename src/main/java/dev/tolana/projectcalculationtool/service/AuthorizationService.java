package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import dev.tolana.projectcalculationtool.model.Role;
import dev.tolana.projectcalculationtool.repository.AuthorizationRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("auth")
public class AuthorizationService {

    private AuthorizationRepository authorizationRepository;
    private Map<Long, Role> roles;

    public AuthorizationService(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
        roles = initRoles();
    }

    private Map<Long, Role> initRoles() {
        Map<Long, Role> roles = new HashMap<>();

    }

    public boolean hasAccess() {
        return true;
    }

    private HierarchyDto getHierarchy(long id, AccessLevel accessLevel) {
        return authorizationRepository.getHierarchy(id,accessLevel);
    }
}
