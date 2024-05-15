package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.enums.AccessLevel;
import dev.tolana.projectcalculationtool.enums.Permission;
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
    private final String HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE task_id = ? OR project_id = ? OR team_id = ? OR department_id = ? OR organisation_id = ? LIMIT 1;";
    private final String USER_ENTITY_ROLE_SQL = "SELECT role_id FROM user_entity_role WHERE username = ? AND (organisation_id = ? OR department_id = ? OR team_id = ? OR project_id = ? OR task_id = ?)";
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

    public HierarchyDto getHierarchy(long id) {
        HierarchyDto hierarchyDto = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(HIERARCHY_SQL);
            for (int i = 1; i <= 5; i++) {
                preparedStatement.setLong(i, id);
            }
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

    public List<Long> getRoleIdsMatchingHierarchy(String username, HierarchyDto hierarchy) {
        List<Long> roleIds = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(USER_ENTITY_ROLE_SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setLong(2, hierarchy.organizationId());
            preparedStatement.setLong(3, hierarchy.departmentId());
            preparedStatement.setLong(4, hierarchy.teamId());
            preparedStatement.setLong(5, hierarchy.projectId());
            preparedStatement.setLong(6, hierarchy.taskId());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roleIds.add(resultSet.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return roleIds;
    }
}
