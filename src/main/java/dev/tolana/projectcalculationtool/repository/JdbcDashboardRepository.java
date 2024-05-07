package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.model.Project;
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
                        resultSet.getInt("organization_id")
                );
                userEntityRoleDtoList.add(userEntityRoleDto);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return userEntityRoleDtoList;
    }

    @Override
    public int addProject(Project project){
        int projectId = -1;

        try (Connection connection = dataSource.getConnection()) {
            String insertNewProject = "INSERT INTO project (name, description, team_id," +
                                      "allotted_hours, status) " +
                                      "VALUES (?,?,?,?,?);";

            PreparedStatement pstmt = connection.prepareStatement(insertNewProject,
                    Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setInt(3, project.getTeam_id());
            pstmt.setInt(4, project.getAllotted_hours());
            pstmt.setInt(5, project.getStatus());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                projectId = generatedKeys.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return projectId;
    };
}
