package dev.tolana.projectcalculationtool.service;


import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.constant.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthorizationServiceTests {

    @Autowired
    private AuthorizationService auth;

    @WithMockUser("masiomasu")
    @Test
    public void OrganisationAdminCannotDeleteOrganisation() {
        assertFalse(auth.hasAccess(1, AccessLevel.ORGANIZATION, Permission.DELETE));
    }

    @WithMockUser("tolana")
    @Test
    public void OrganisationOwnerCanDeleteOrganisation() {
        assertTrue(auth.hasAccess(1, AccessLevel.ORGANIZATION, Permission.DELETE));
    }

    @WithMockUser("masiomasu")
    @Test
    public void OrganisationAdminCanDeleteDepartment() {
        assertTrue(auth.hasAccess(2, AccessLevel.DEPARTMENT, Permission.DELETE));
    }
}
