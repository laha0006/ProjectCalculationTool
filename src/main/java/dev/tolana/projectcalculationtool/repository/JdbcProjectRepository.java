package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcProjectRepository implements EntityCrudOperations {

    private DataSource dataSource;

    public JdbcProjectRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createEntity(String username, Entity project) {
        boolean isCreated;

        try (Connection connection = dataSource.getConnection()) {
            String insertNewProject = "INSERT INTO project (name, description, team_id," +
                    "allotted_hours, status) " +
                    "VALUES (?,?,?,?,?);";

            PreparedStatement pstmt = connection.prepareStatement(insertNewProject,
                    Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setLong(3, ((Project)project).getTeamId());
            pstmt.setLong(4, ((Project)project).getAllottedHours());
            pstmt.setLong(5, ((Project)project).getStatusId()); //TODO DEMETERS LOV
            int affectedRows = pstmt.executeUpdate();
            isCreated = affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return isCreated;
    }

    @Override
    public Entity getEntityOnId(long projectId) {
        Entity project = null;
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
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(10),
                        rs.getTimestamp(6).toLocalDateTime(),
                        Status.valueOf(rs.getString(8)),
                        rs.getLong(9),
                        rs.getLong(4),
                        rs.getInt(7)
                );
            }
        }catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return project;
    }

    @Override
    public List<Entity> getAllEntitiesOnUsername(String username) {
        List<Entity> projectList = new ArrayList<>();
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
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Entity project = new Project(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(10),
                        rs.getTimestamp(6).toLocalDateTime(),
                        Status.valueOf(rs.getString(8)),
                        rs.getLong(9),
                        rs.getLong(4),
                        rs.getInt(7)
                );
                projectList.add(project);
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return projectList;
    }

    @Override
    public List<Entity> getAllEntitiesOnId(long entityId) {
        return null;
    }

    @Override
    public boolean editEntity(Entity entity) {
        return false;
    }

    @Override
    public boolean deleteEntity(long entityId) {
        return false;
    }

    @Override
    public boolean inviteToEntity(String inviteeUsername) {
        return false;
    }

    @Override
    public boolean archiveEntity(long entityId, boolean isArchived) {
        return false;
    }

    @Override
    public boolean assignUser(long projectId, List<String> selectedTeamMembers, UserRole role) {
       boolean isAssigned = false;

        String assignTeamMembersToProject = """
                INSERT INTO user_entity_role (username, role_id, project_id) VALUES (?, ?, ?);
                """;
        long roleId = role.getRoleId();

        try (Connection connection = dataSource.getConnection()) {
            try{
                connection.setAutoCommit(false);

                for (String member :selectedTeamMembers) {
                    PreparedStatement pstmt = connection.prepareStatement(assignTeamMembersToProject);
                    pstmt.setString(1, member);
                    pstmt.setLong(2, roleId);
                    pstmt.setLong(3, projectId);
                    int affectedRows = pstmt.executeUpdate();
                    isAssigned = affectedRows > 0;
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

        return isAssigned;
    }

    @Override
    public List<UserInformationDto> getUsersFromEntityId(long teamId) {
        List<UserInformationDto> userInformationDtoList = new ArrayList<>();
        String getTeamMembersFromTeamId = """
               
                SELECT uer.*
               FROM user_entity_role AS uer
               LEFT JOIN user_entity_role AS uer_project
                   ON uer.username = uer_project.username
                   AND uer_project.project_id IS NOT NULL
               WHERE uer.team_id = ?
                   AND uer_project.username IS NULL;
                """;
        //TODO THIS QUERY MIGHT NOT WORK
        //TODO OLD QUERY
//        SELECT uer.username
//        FROM user_entity_role AS uer
//        WHERE uer.team_id = 1
//        AND uer.username NOT IN (
//                SELECT username
//                FROM user_entity_role
//                WHERE project_id = 1);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getTeamMembersFromTeamId);
            pstmt.setLong(1, teamId);
//            pstmt.setLong(2, projectId);
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
}
