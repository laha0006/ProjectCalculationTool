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
        assertFalse(auth.hasAccess(1, AccessLevel.ORGANISATION, Permission.DELETE));
    }

    @WithMockUser("tolana")
    @Test
    public void OrganisationOwnerCanDeleteOrganisation() {
        assertTrue(auth.hasAccess(1, AccessLevel.ORGANISATION, Permission.DELETE));
    }

    @WithMockUser("masiomasu")
    @Test
    public void departmentOwnerCanDeleteDepartment() {
        assertTrue(auth.hasAccess(2, AccessLevel.DEPARTMENT, Permission.DELETE));
    }


    @WithMockUser("vz")
    @Test
    public void departmentOwnerCannotDeleteParentOrganization() {
        assertFalse(auth.hasAccess(2, AccessLevel.ORGANISATION, Permission.DELETE));
    }

    @WithMockUser("tolana")
    @Test
    public void organizationOwnerCanReadEverything() {
        assertTrue(auth.hasAccess(1, AccessLevel.ORGANISATION, Permission.READ));
        assertTrue(auth.hasAccess(1, AccessLevel.DEPARTMENT, Permission.READ));
        assertTrue(auth.hasAccess(1, AccessLevel.TEAM, Permission.READ));
        assertTrue(auth.hasAccess(1, AccessLevel.PROJECT, Permission.READ));
        assertTrue(auth.hasAccess(1, AccessLevel.TASK, Permission.READ));
    }
}
