package dev.tolana.projectcalculationtool.service;


import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.constant.Permission;
import dev.tolana.projectcalculationtool.constant.UserRole;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthorizationServiceTests {

    @Autowired
    private AuthorizationService auth;

    @Autowired
    private DataSource dataSource;

    private Connection con;

    @BeforeEach
    public void setUp() throws SQLException {
        con = dataSource.getConnection();
    }

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

    @WithMockUser("TheNewGuy")
    @Test
    public void assignRole() throws SQLException {
        RoleAssignUtil.assignDepartmentRole(con,1,UserRole.ADMIN,"TheNewGuy");  // admin of Dept 1
        assertFalse(auth.hasAccess(1, AccessLevel.DEPARTMENT, Permission.DELETE));
    }
}
