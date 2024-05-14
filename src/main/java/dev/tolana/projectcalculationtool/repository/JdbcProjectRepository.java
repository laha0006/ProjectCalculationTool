package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Status;
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
            pstmt.setLong(5, project.getStatus().getId());
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
                SELECT p.id,
                       p.name,
                       p.description,
                       p.team_id,
                       p.date_created,
                       p.deadline,
                       p.allotted_hours,
                       s.name,
                       p.parent_id,
                       p.archived
                FROM project p
                     JOIN      user_entity_role uer
                               ON uer.project_id = p.id
                     JOIN      users
                               ON uer.username = users.username
                     LEFT JOIN status s
                               ON p.status = s.id
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
                        Status.valueOf(projectsRs.getString(8)),
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
    public List<UserInformationDto> getTeamMembersFromTeamId(long teamId, long projectId) {
        List<UserInformationDto> userInformationDtoList = new ArrayList<>();
        String getTeamMembersFromTeamId = """
                SELECT uer.username
                FROM user_entity_role AS uer
                WHERE uer.team_id = ?
                AND uer.username NOT IN (
                SELECT username
                FROM user_entity_role
                WHERE project_id = ?);
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getTeamMembersFromTeamId);
            pstmt.setLong(1, teamId);
            pstmt.setLong(2, projectId);
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
                INSERT INTO user_entity_role (username, role_id, project_id) VALUES (?, ?, ?);
                """;
        int roleUser = 3;
        try (Connection connection = dataSource.getConnection()) {
            try{
                connection.setAutoCommit(false);

                for (String member :selectedTeamMembers) {
                    PreparedStatement pstmt = connection.prepareStatement(assignTeamMembersToProject);
                    pstmt.setString(1, member);
                    pstmt.setLong(2, roleUser);
                    pstmt.setLong(3, projectId);
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

    @Override
    public Project getProjectOnId(long projectId) {
       Project project = null;
        String selectProjectOnId = """
                SELECT p.id,
                       p.name,
                       p.description,
                       p.team_id,
                       p.date_created,
                       p.deadline,
                       p.allotted_hours,
                       s.name,
                       p.parent_id,
                       p.archived
                FROM project p
                     LEFT JOIN status s
                          ON p.status = s.id
                WHERE p.id = ?;
                """;

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(selectProjectOnId);
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                project = new Project(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getLong(4),
                        rs.getTimestamp(5),
                        rs.getTimestamp(6),
                        rs.getInt(7),
                        Status.valueOf(rs.getString(8)),
                        rs.getLong(9),
                        rs.getBoolean(10)
                );
            }
        }catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return project;
    }
}
