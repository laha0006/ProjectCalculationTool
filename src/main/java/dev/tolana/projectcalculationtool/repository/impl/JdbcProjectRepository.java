package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.model.Team;
import dev.tolana.projectcalculationtool.repository.EntityCrudOperations;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class JdbcProjectRepository implements ProjectRepository {

    private DataSource dataSource;

    public JdbcProjectRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createEntity(String username, Entity project) {
        boolean isCreated = false;

        try (Connection connection = dataSource.getConnection()) {
            String insertNewProject = "INSERT INTO project (name, description, team_id," +
                                      "status, deadline) " +
                                      "VALUES (?,?,?,?,?);";

            PreparedStatement pstmt = connection.prepareStatement(insertNewProject,
                    Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setLong(3, ((Project)project).getTeamId());
            pstmt.setLong(4, ((Project)project).getStatusId());
            pstmt.setDate(5,Date.valueOf(((Project) project).getDeadline().toLocalDate()));
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            long projectId;
            if (generatedKeys.next()) {
                projectId = generatedKeys.getLong(1);
                RoleAssignUtil.assignProjectRole(connection, projectId, UserRole.PROJECT_OWNER, username);
                isCreated = true;
            }

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
    public List<Entity> getChildren(long projectId) {
        List<Entity> taskList = new ArrayList<>();
        String getTasks = """
                SELECT t.id,
                       t.name,
                       t.description,
                       t.project_id,
                       t.date_created,
                       t.deadline,
                       t.estimated_hours,
                       t.actual_hours,
                       s.name,
                       t.parent_id,
                       t.archived
                FROM task t
                     LEFT JOIN status s
                               ON t.status = s.id
                WHERE t.project_id = ? AND t.parent_id IS NULL;
                """;

        try (Connection connection = dataSource.getConnection()){
            PreparedStatement pstmt = connection.prepareStatement(getTasks);
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(11),
                        rs.getTimestamp(6).toLocalDateTime(),
                        Status.valueOf(rs.getString(9)),
                        rs.getLong(10),
                        rs.getLong(4),
                        rs.getInt(7),
                        rs.getInt(8)
                );
                taskList.add(task);
            }
        }catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return taskList;
    }

    @Override
    public boolean editEntity(Entity entity) {
        return false;
    }

    @Override
    public boolean deleteEntity(long projectId) {
        boolean isDeleted;
        System.out.println("IN DELETE ENTITY");
        String deleteProject = """
                DELETE FROM project
                WHERE project.id = ?;
                """;
        try (Connection connection = dataSource.getConnection()){
            try {
                connection.setAutoCommit(false);
                PreparedStatement pstmt = connection.prepareStatement(deleteProject);
                pstmt.setLong(1, projectId);
                int affectedRows = pstmt.executeUpdate();

                isDeleted = affectedRows > 0;

                connection.commit();
                connection.setAutoCommit(true);

            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new RuntimeException(sqlException);
            }
        }catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return isDeleted;
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

    @Override
    public List<UserRole> getAllUserRoles() {
        List<UserRole> roles = new ArrayList<>(2);
        String getAllUserRoles = """
                SELECT name
                FROM role
                WHERE name = 'PROJECT_ADMIN' OR name = 'PROJECT_MEMBER';
                """;
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getAllUserRoles);
            ResultSet userRoles = pstmt.executeQuery();

            while (userRoles.next()) {
                roles.add(UserRole.valueOf(userRoles.getString(1)));
            }

        }catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return roles;
    }

    @Override
    public boolean changeStatus(long resourceEntityId) {
        return false;
    }
}
