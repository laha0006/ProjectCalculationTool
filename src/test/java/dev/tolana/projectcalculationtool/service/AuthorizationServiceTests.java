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
        if (con == null) {
            con = dataSource.getConnection();
        }
    }

    @WithMockUser("masiomasu")
    @Test
    public void OrganisationAdminCannotDeleteOrganisation() {
        assertFalse(auth.hasOrgansiationAccess(1, Permission.ORGANISATION_DELETE));
    }

    @WithMockUser("tolana")
    @Test
    public void OrganisationOwnerCanDeleteOrganisation() {
        assertTrue(auth.hasOrgansiationAccess(1, Permission.ORGANISATION_DELETE));
    }

    @WithMockUser("masiomasu")
    @Test
    public void departmentOwnerCanDeleteDepartment() {
        assertTrue(auth.hasDepartmentAccess(2, Permission.DEPARTMENT_DELETE));
    }


    @WithMockUser("vz")
    @Test
    public void departmentOwnerCannotDeleteParentOrganization() {
        assertFalse(auth.hasOrgansiationAccess(2, Permission.ORGANISATION_DELETE));
    }

    @WithMockUser("tolana")
    @Test
    public void organizationOwnerCanReadEverything() {
        assertTrue(auth.hasOrgansiationAccess(1, Permission.ORGANISATION_READ));
        assertTrue(auth.hasDepartmentAccess(1, Permission.DEPARTMENT_READ));
        assertTrue(auth.hasTeamAccess(1, Permission.TEAM_READ));
        assertTrue(auth.hasProjectAccess(1, Permission.PROJECT_READ));
        assertTrue(auth.hasTaskAccess(1, Permission.TASK_READ));
    }

    @WithMockUser("TheNewGuy")
    @Test
    public void assignRole() throws SQLException {
        RoleAssignUtil.assignDepartmentRole(con, 1, UserRole.DEPARTMENT_ADMIN, "TheNewGuy");  // admin of Dept 1
        assertFalse(auth.hasDepartmentAccess(1, Permission.DEPARTMENT_DELETE));
    }

    @WithMockUser("TheNewGuy")
    @Test
    public void weightedCheck() throws SQLException {
        RoleAssignUtil.assignProjectRole(con, 1, UserRole.PROJECT_OWNER, "someguy");  // owner of project 1
        RoleAssignUtil.assignProjectRole(con, 1, UserRole.PROJECT_ADMIN, "TheNewGuy");  // admin of project 1
        assertFalse(auth.hasProjectAccess(1, Permission.PROJECT_DELETE));
    }

//    @Test
//    @WithMockUser("userA")
//    public void canKickOrgMemberFromProjectAsProjectAdmin() throws SQLException {
//        RoleAssignUtil.assignProjectRole(con, 1, UserRole.PROJECT_ADMIN, "userA");
//        RoleAssignUtil.assignOrganisationRole(con, 1, UserRole.ORGANISATION_MEMBER, "userB");
//        assertTrue(auth.canPerform(1, "userB", Permission.PROJECT_KICK));
//    }

//    @Test
//    @WithMockUser("userA")
//    public void subProjectTest() throws SQLException {
//        RoleAssignUtil.assignProjectRole(con, 1, UserRole.PROJECT_MEMBER, "userA");
//        assertTrue(auth.hasProjectAccess(1, Permission.PROJECT_READ));
//    }


}
