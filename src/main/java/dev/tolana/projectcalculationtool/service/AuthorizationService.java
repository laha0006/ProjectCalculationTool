package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.repository.AuthorizationRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private AuthorizationRepository authorizationRepository;

    public AuthorizationService(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    private getHierarchy(long id, AccessLevel) {

    }
}
