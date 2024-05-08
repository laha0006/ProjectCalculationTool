package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import dev.tolana.projectcalculationtool.repository.AuthorizationRepository;
import org.springframework.stereotype.Service;

@Service("auth")
public class AuthorizationService {

    private AuthorizationRepository authorizationRepository;

    public AuthorizationService(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    public boolean hasAccess() {
        return true;
    }

    private HierarchyDto getHierarchy(long id, AccessLevel accessLevel) {
        return authorizationRepository.getHierarchy(id,accessLevel);
    }
}
