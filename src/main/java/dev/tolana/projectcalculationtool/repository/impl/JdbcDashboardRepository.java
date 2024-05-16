package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.repository.DashboardRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcDashboardRepository implements DashboardRepository {

    private DataSource dataSource;

    public JdbcDashboardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<UserEntityRoleDto> getUserEntityRoleListOnUsername(String username) {
        List<UserEntityRoleDto> userEntityRoleDtoList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String getAuthenticatedUser = """
                    SELECT * FROM user_entity_role
                    WHERE username = ?;
                    """;
            PreparedStatement pstmt = connection.prepareStatement(getAuthenticatedUser);
            pstmt.setString(1, username);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                UserEntityRoleDto userEntityRoleDto = new UserEntityRoleDto(
                        resultSet.getString("username"),
                        resultSet.getInt("role_id"),
                        resultSet.getInt("task_id"),
                        resultSet.getInt("project_id"),
                        resultSet.getInt("team_id"),
                        resultSet.getInt("department_id"),
                        resultSet.getInt("organisation_id")
                );
                userEntityRoleDtoList.add(userEntityRoleDto);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return userEntityRoleDtoList;
    }
}
