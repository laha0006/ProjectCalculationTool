package dev.tolana.projectcalculationtool.service;


import dev.tolana.projectcalculationtool.enums.Permission;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
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
        assertFalse(auth.hasAccess(1, Permission.ORGANISATION_DELETE));
    }

    @WithMockUser("tolana")
    @Test
    public void OrganisationOwnerCanDeleteOrganisation() {
        assertTrue(auth.hasAccess(1, Permission.ORGANISATION_DELETE));
    }

    @WithMockUser("masiomasu")
    @Test
    public void departmentOwnerCanDeleteDepartment() {
        assertTrue(auth.hasAccess(2, Permission.DEPARTMENT_DELETE));
    }


    @WithMockUser("vz")
    @Test
    public void departmentOwnerCannotDeleteParentOrganization() {
        assertFalse(auth.hasAccess(2, Permission.ORGANISATION_DELETE));
    }

    @WithMockUser("tolana")
    @Test
    public void organizationOwnerCanReadEverything() {
        assertTrue(auth.hasAccess(1, Permission.ORGANISATION_READ));
        assertTrue(auth.hasAccess(1, Permission.DEPARTMENT_READ));
        assertTrue(auth.hasAccess(1, Permission.TEAM_READ));
        assertTrue(auth.hasAccess(1, Permission.PROJECT_READ));
        assertTrue(auth.hasAccess(1, Permission.TASK_READ));
    }

    @WithMockUser("TheNewGuy")
    @Test
    public void assignRole() throws SQLException {
        RoleAssignUtil.assignDepartmentRole(con, 1, UserRole.DEPARTMENT_ADMIN, "TheNewGuy");  // admin of Dept 1
        assertFalse(auth.hasAccess(1, Permission.DEPARTMENT_DELETE));
    }

    @WithMockUser("TheNewGuy")
    @Test
    public void weightedCheck() throws SQLException {
        RoleAssignUtil.assignProjectRole(con, 1, UserRole.PROJECT_OWNER, "someguy");  // owner of project 1
        RoleAssignUtil.assignProjectRole(con, 1, UserRole.PROJECT_ADMIN, "TheNewGuy");  // admin of project 1
        assertFalse(auth.hasAccess(1, Permission.PROJECT_DELETE));
    }
    //userA trys to kick userB, should fail since userB is Owner, and A is only Admin.
    @Test
    @WithMockUser("userA")
    public void cannotKickOwner() throws SQLException {
        RoleAssignUtil.assignOrganisationRole(con,1, UserRole.ORGANISATION_ADMIN,"userA");
        RoleAssignUtil.assignOrganisationRole(con,1, UserRole.ORGANISATION_OWNER,"userB");
        assertFalse(auth.canPerform(1,"userB",Permission.ORGANISATION_KICK));
    }


}
