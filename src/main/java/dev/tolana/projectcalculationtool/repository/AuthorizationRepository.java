package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.dto.HierarchyDto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AuthorizationRepository {
    private final String TASK_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE task_id = ?";
    private final String PROJECT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE project_id = ?";
    private final String TEAM_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE team_id = ?";
    private final String DEPARTMENT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE department_id = ?";
    private final String ORGANIZATION_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE organization_id = ?";

    private DataSource dataSource;

    public AuthorizationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public HierarchyDto getHierarchy(long id, AccessLevel accessLevel) {
        HierarchyDto hierarchyDto = null;
        String SQL = switch(accessLevel) {
            case TASK -> TASK_HIERARCHY_SQL;
            case PROJECT -> PROJECT_HIERARCHY_SQL;
            case TEAM -> TEAM_HIERARCHY_SQL;
            case DEPARTMENT -> DEPARTMENT_HIERARCHY_SQL;
            case ORGANIZATION -> ORGANIZATION_HIERARCHY_SQL;
        };
        try(Connection connection = dataSource.getConnection()) {
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


}
