package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.model.Project;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcProjectRepository implements ProjectRepository {

    private DataSource dataSource;

    public JdbcProjectRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int addProject(Project project) {
        int projectId = -1;

        try (Connection connection = dataSource.getConnection()) {
            String insertNewProject = "INSERT INTO project (name, description, team_id," +
                    "allotted_hours, status) " +
                    "VALUES (?,?,?,?,?);";

            PreparedStatement pstmt = connection.prepareStatement(insertNewProject,
                    Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setLong(3, project.getTeamId());
            pstmt.setLong(4, project.getAllottedHours());
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
    }

    @Override
    public List<Project> getAllProjectsOnUsername(String username) {
        List<Project> projectList = new ArrayList<>();
        String getAllProjects = """
                SELECT\s
                project.id,\s
                project.name,\s
                project.description,\s
                project.team_id,\s
                project.date_created,\s
                project.deadline,\s
                project.allotted_hours,\s
                project.status,\s
                project.parent_id,\s
                project.archived\s
                                
                FROM project
                JOIN user_entity_role ON user_entity_role.project_id = project.id
                JOIN users ON user_entity_role.username = users.username
                WHERE users.username = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getAllProjects);
            pstmt.setString(1, username);
            ResultSet projectsRs = pstmt.executeQuery();

            while (projectsRs.next()) {
                Project project = new Project(
                        projectsRs.getLong(1),
                        projectsRs.getString(2),
                        projectsRs.getString(3),
                        projectsRs.getLong(4),
                        projectsRs.getTimestamp(5),
                        projectsRs.getTimestamp(6),
                        projectsRs.getInt(7),
                        projectsRs.getInt(8),
                        projectsRs.getLong(9),
                        projectsRs.getBoolean(10)
                );
                projectList.add(project);
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return projectList;
    }

    @Override
    public long getTeamIdFromUsername(String username) {
        long teamId = -1;
        String getTeamIdFromUsername = """
                SELECT t.id FROM team t
                JOIN user_entity_role uer ON uer.team_id = t.id
                JOIN users ON users.username = uer.username\s
                WHERE users.username = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getTeamIdFromUsername);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                teamId = rs.getLong(1);
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return teamId;
    }

    @Override
    public List<UserInformationDto> getTeamMembersFromTeamId(long teamId) {
        List<UserInformationDto> userInformationDtoList = new ArrayList<>();
        String getTeamMembersFromTeamId = """
                SELECT uer.username
                FROM user_entity_role AS uer
                WHERE uer.team_id = ?
                AND uer.username NOT IN (
                SELECT username
                FROM user_entity_role
                WHERE project_id IS NOT NULL);
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getTeamMembersFromTeamId);
            pstmt.setLong(1, teamId);
            ResultSet teamMembersRs = pstmt.executeQuery();

            while (teamMembersRs.next()) {
                UserInformationDto member = new UserInformationDto(
                        teamMembersRs.getString(1)
                );
                userInformationDtoList.add(member);
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return userInformationDtoList;
    }

    @Override
    public void assignTeamMembersToProject(long projectId, List<String> selectedTeamMembers) {
        String assignTeamMembersToProject = """
                INSERT INTO user_entity_role (username, project_id) VALUES (?, ?);
                """;

        try (Connection connection = dataSource.getConnection()) {
            try{
                connection.setAutoCommit(false);

                for (String member :selectedTeamMembers) {
                    PreparedStatement pstmt = connection.prepareStatement(assignTeamMembersToProject);
                    pstmt.setString(1, member);
                    pstmt.setLong(2, projectId);
                    pstmt.executeUpdate();
                }

                connection.commit();
                connection.setAutoCommit(true);

            } catch (Exception exception) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}
