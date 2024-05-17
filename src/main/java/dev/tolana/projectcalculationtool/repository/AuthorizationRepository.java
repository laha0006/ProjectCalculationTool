package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.InviteDto;
import dev.tolana.projectcalculationtool.enums.AccessLevel;
import dev.tolana.projectcalculationtool.enums.Permission;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.exception.InviteFailureException;
import dev.tolana.projectcalculationtool.model.Role;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class AuthorizationRepository {
    private final String ORGANISATION_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE organisation_id = ? LIMIT 1;";
    private final String DEPARTMENT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE department_id = ? LIMIT 1;";
    private final String TEAM_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE team_id = ? LIMIT 1;";
    private final String PROJECT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE project_id = ? LIMIT 1;";
    private final String TASK_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE task_id = ? LIMIT 1;";
    private final String USER_ENTITY_ROLE_SQL = "SELECT role_id FROM user_entity_role WHERE username = ? AND (organisation_id = ? OR department_id = ? OR team_id = ? OR project_id IN (?,?) OR task_id IN (?,?));\n";
    private final String INVITATIONS_SQL = "SELECT o.name, o.description, o.id FROM invitation i JOIN organisation o ON o.id = i.organisation_iu WHERE username = ?;";
    private final String DELETE_INVITE_SQL = "DELETE FROM invitation WHERE username = ? AND organisation_iu = ?";
    private final String CHECK_USER_IN_ORGANISATION_SQL = "SELECT username FROM user_entity_role WHERE username = ? AND organisation_id = ?;";
    private final String CREATE_INVITE_SQL = "INSERT INTO invitation VALUES(?,?);";
    private final String ROLES_PERMISSIONS_SQL = """
            SELECT r.id   AS role_id,
                   r.name AS role_name,
                   r.weight AS weight,
                   p.name AS perm_name
            FROM role r
                 LEFT JOIN role_permission rp
                           ON r.id = rp.role_id
                 LEFT JOIN permission p
                           ON rp.perm_id = p.id
            """;

    private DataSource dataSource;

    public AuthorizationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public HierarchyDto getHierarchy(long id, AccessLevel accessLevel) {
        HierarchyDto hierarchyDto = null;
        String SQL = switch (accessLevel) {
            case TASK -> TASK_HIERARCHY_SQL;
            case PROJECT -> PROJECT_HIERARCHY_SQL;
            case TEAM -> TEAM_HIERARCHY_SQL;
            case DEPARTMENT -> DEPARTMENT_HIERARCHY_SQL;
            case ORGANISATION -> ORGANISATION_HIERARCHY_SQL;
        };
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                hierarchyDto = new HierarchyDto(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getLong(3),
                        resultSet.getLong(4),
                        resultSet.getLong(5),
                        resultSet.getLong(6),
                        resultSet.getLong(7)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return hierarchyDto;
    }

    public Map<Long, Role> getRoles() {
        Map<Long, Role> roles = new HashMap<>();
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(ROLES_PERMISSIONS_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long roleId = resultSet.getLong(1);
                if (!roles.containsKey(roleId)) {
                    String roleName = resultSet.getString(2);
                    short weight = resultSet.getShort(3);
                    roles.put(roleId, new Role(roleId, roleName, weight));
                }
                if (resultSet.getString(4) != null) {
                    Permission perm = Permission.valueOf(resultSet.getString(4));
                    roles.get(roleId).addPermission(perm);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return roles;
    }

    public List<Long> getRoleIdsMatchingHierarchy(String username, HierarchyDto hierarchy) {
        List<Long> roleIds = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(USER_ENTITY_ROLE_SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setLong(2, hierarchy.organizationId());
            preparedStatement.setLong(3, hierarchy.departmentId());
            preparedStatement.setLong(4, hierarchy.teamId());
            preparedStatement.setLong(5, hierarchy.projectId());
            preparedStatement.setLong(6, hierarchy.parentProjectId());
            preparedStatement.setLong(7, hierarchy.taskId());
            preparedStatement.setLong(8, hierarchy.parentTaskId());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roleIds.add(resultSet.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return roleIds;
    }

    public List<InviteDto> getInvitations(String username) {
        List<InviteDto> invitations = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(INVITATIONS_SQL);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                invitations.add(new InviteDto(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getLong(3)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return invitations;
    }

    public void addUserToOrganisation(String username, long organisationId) {
        try (Connection con = dataSource.getConnection()) {
            try {
                con.setAutoCommit(false);
                PreparedStatement preparedStatement = con.prepareStatement(DELETE_INVITE_SQL);
                preparedStatement.setString(1, username);
                preparedStatement.setLong(2, organisationId);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    con.rollback();
                    con.setAutoCommit(true);
                    throw new InviteFailureException("Kunne ikke acceptere invitationen!");
                }
                RoleAssignUtil.assignOrganisationRole(con, organisationId, UserRole.ORGANISATION_MEMBER, username);
            } catch(SQLException e) {
                con.rollback();
                con.setAutoCommit(true);
                throw new InviteFailureException("Kunne ikke acceptere invitationen!");
            }
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeInvite(String username, long orgId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(DELETE_INVITE_SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setLong(2, orgId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new InviteFailureException("Noget gik galt, kunne ikke afvise invitation!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createInvite(String username, long organisationId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(CREATE_INVITE_SQL);
            preparedStatement.setString(1,username);
            preparedStatement.setLong(2, organisationId);
            int affectedRows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new InviteFailureException("Brugeren findes ikke, eller er allerede inviteret!");
            }
            throw new RuntimeException(e);
        }
    }

    public int checkUserInOrganisation(String username, long orgId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(CHECK_USER_IN_ORGANISATION_SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setLong(2, orgId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
}
