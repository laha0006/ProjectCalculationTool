package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.constant.Permission;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.model.Role;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class AuthorizationRepository {
    private final String TASK_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE task_id = ? LIMIT 1";
    private final String PROJECT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE project_id = ? LIMIT 1";
    private final String TEAM_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE team_id = ? LIMIT 1";
    private final String DEPARTMENT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE department_id = ? LIMIT 1";
    private final String ORGANIZATION_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE organization_id = ? LIMIT 1";

    private final String ORGANIZATION_ROLE_SQL = "SELECT * FROM user_entity_role WHERE username = ? AND organisation_id = ? LIMIT 1";
    private final String DEPARTMENT_ROLE_SQL = "SELECT * FROM user_entity_role WHERE username = ? AND (organisation_id = ? OR department_id = ?) LIMIT 1";
    private final String TEAM_ROLE_SQL = "SELECT * FROM user_entity_role WHERE username = ? AND (organisation_id = ? OR department_id = ? OR team_id = ?) = ? LIMIT 1";
    private final String PROJECT_ROLE_SQL = "SELECT * FROM user_entity_role WHERE username = ? AND (organisation_id = ? OR department_id = ? OR team_id = ? OR project_id = ?) LIMIT 1";
    private final String TASK_ROLE_SQL = "SELECT * FROM user_entity_role WHERE username = ? AND (organisation_id = ? OR department_id = ? OR team_id = ? OR project_id = ? OR task_id = ?) LIMIT 1";

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
            case ORGANIZATION -> ORGANIZATION_HIERARCHY_SQL;
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
                        resultSet.getByte(5)
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


    public List<UserEntityRoleDto> getRoleIdsMatchingHierarchy(String username, HierarchyDto hierarchy, AccessLevel accessLevel) {
        List<UserEntityRoleDto> roles = new ArrayList<>();
        String SQL = switch (accessLevel) {
            case TASK -> TASK_ROLE_SQL;
            case PROJECT -> PROJECT_ROLE_SQL;
            case TEAM -> TEAM_ROLE_SQL;
            case DEPARTMENT -> DEPARTMENT_ROLE_SQL;
            case ORGANIZATION -> ORGANIZATION_ROLE_SQL;
        };
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            switch (accessLevel) {
                case TASK -> {
                    preparedStatement.setLong(2, hierarchy.organizationId());
                    preparedStatement.setLong(3, hierarchy.departmentId());
                    preparedStatement.setLong(3, hierarchy.teamId());
                    preparedStatement.setLong(4, hierarchy.projectId());
                    preparedStatement.setLong(5, hierarchy.taskId());
                }
                case PROJECT -> {
                    preparedStatement.setLong(2, hierarchy.organizationId());
                    preparedStatement.setLong(3, hierarchy.departmentId());
                    preparedStatement.setLong(3, hierarchy.teamId());
                    preparedStatement.setLong(4, hierarchy.projectId());
                }
                case TEAM -> {
                    preparedStatement.setLong(2, hierarchy.organizationId());
                    preparedStatement.setLong(3, hierarchy.departmentId());
                    preparedStatement.setLong(3, hierarchy.teamId());
                }
                case DEPARTMENT -> {
                    preparedStatement.setLong(2, hierarchy.organizationId());
                    preparedStatement.setLong(3, hierarchy.departmentId());
                }
                case ORGANIZATION -> {
                    preparedStatement.setLong(2, hierarchy.organizationId());
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roles.add(new UserEntityRoleDto(
                        resultSet.getString(2),
                        resultSet.getLong(3),
                        resultSet.getLong(4),
                        resultSet.getLong(5),
                        resultSet.getLong(6),
                        resultSet.getLong(7),
                        resultSet.getLong(8)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return roles;
    }
}
