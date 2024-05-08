package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.constant.Permission;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import dev.tolana.projectcalculationtool.model.Role;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AuthorizationRepository {
    private final String TASK_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE task_id = ?";
    private final String PROJECT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE project_id = ?";
    private final String TEAM_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE team_id = ?";
    private final String DEPARTMENT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE department_id = ?";
    private final String ORGANIZATION_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE organization_id = ?";
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
                    roles.put(roleId, new Role(roleId,roleName,weight));
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


}
