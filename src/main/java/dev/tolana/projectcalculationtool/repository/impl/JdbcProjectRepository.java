package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
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
    public boolean createEntity(String username, Entity project) {
        boolean isCreated;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                String insertNewProject = determineCreationQueryType(((Project) project).getParentId());
                PreparedStatement pstmt = connection.prepareStatement(insertNewProject,
                        Statement.RETURN_GENERATED_KEYS);

                ResultSet generatedKey = setValues(pstmt, project);

                long projectId;
                if (generatedKey.next()) {
                    projectId = generatedKey.getLong(1);
                    RoleAssignUtil.assignProjectRole(connection, projectId, UserRole.PROJECT_OWNER, username);
                    isCreated = true;
                } else {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    throw new EntityException("Projekt blev ikke oprettet, noget gik galt!", Alert.DANGER);
                }


            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                if (sqlException instanceof DataTruncation) {
                    throw new EntityException("Projekt blev ikke oprettet, navn eller beskrivelse er for lang!", Alert.WARNING);
                }
                throw new EntityException("Projekt blev ikke oprettet, noget gik galt!", Alert.DANGER);
            }
            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            throw new EntityException("Projekt blev ikke oprettet, noget gik galt!", Alert.DANGER);
        }

        return isCreated;
    }

    private String determineCreationQueryType(long idSpecifier) {

        if (idSpecifier == 0) {
            //query for project
            return "INSERT INTO project (name, description, team_id, deadline, allotted_hours) VALUES (?,?,?,?,?);";
        } else {
            //query for subproject creation
            return "INSERT INTO project (name, description, parent_id, team_id, deadline, allotted_hours) VALUES (?,?,?,?,?,?);";
        }
    }

    private ResultSet setValues(PreparedStatement pstmt, Entity project) throws SQLException {

        if (((Project) project).getParentId() == 0) {
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setLong(3, ((Project) project).getTeamId());
            pstmt.setDate(4, Date.valueOf(((Project) project).getDeadline().toLocalDate()));
            pstmt.setInt(5, ((Project) project).getAllottedHours());
            pstmt.executeUpdate();
            return pstmt.getGeneratedKeys();

        } else {
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setLong(3, ((Project) project).getParentId());
            pstmt.setLong(4, ((Project) project).getTeamId());
            pstmt.setDate(5, Date.valueOf(((Project) project).getDeadline().toLocalDate()));
            pstmt.setInt(6, ((Project) project).getAllottedHours());
            pstmt.executeUpdate();
            return pstmt.getGeneratedKeys();
        }
    }

    @Override
    public Entity getEntityOnId(long projectId) {
        Entity project;
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

        try (Connection connection = dataSource.getConnection()) {
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
            } else {
                throw new EntityException("Projekt blev ikke fundet, noget gik galt!", Alert.WARNING);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Projekt blev ikke fundet, noget gik galt!", Alert.WARNING);
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

        try (Connection connection = dataSource.getConnection()) {
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
        } catch (SQLException sqlException) {
            throw new EntityException("Projekt blev hente opgaver, noget gik galt!", Alert.WARNING);
        }

        return taskList;
    }

    @Override
    public Entity getParent(long parentId) {
        return null;
    }

    @Override
    public List<Project> getSubProjects(long projectId) {
        List<Project> subProjects = new ArrayList<>();
        String getTasks = """
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
                WHERE p.parent_id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getTasks);
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("PROJECT ID " + rs.getLong(1));
                Project project = new Project(
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
                subProjects.add(project);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Sub-projekter blev ikke fundet, noget gik galt!", Alert.WARNING);
        }

        return subProjects;
    }


    @Override
    public boolean editEntity(Entity entity) {
        boolean isEdited;
        String edit = """
                UPDATE project SET name=?, description=?, deadline=?, allotted_hours=?, status=? WHERE id =?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(edit);
                pstmt.setString(1, entity.getName());
                pstmt.setString(2, entity.getDescription());
                pstmt.setDate(3, Date.valueOf(((Project) entity).getDeadline().toLocalDate()));
                pstmt.setInt(4, ((Project) entity).getAllottedHours());
                pstmt.setLong(5, ((Project) entity).getStatusId());
                pstmt.setLong(6, entity.getId());
                int affectedRows = pstmt.executeUpdate();

                isEdited = affectedRows > 0;

            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new EntityException("Projekt blev ikke opdateret, noget gik galt!", Alert.DANGER);
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlException) {
            throw new EntityException("Projekt blev ikke opdateret, noget gik galt!", Alert.DANGER);
        }

        return isEdited;
    }

    @Override
    public boolean deleteEntity(long projectId) {
        boolean isDeleted;
        System.out.println("IN DELETE ENTITY");
        String deleteProject = """
                DELETE FROM project
                WHERE project.id = ?;
                """;
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);
                PreparedStatement pstmt = connection.prepareStatement(deleteProject);
                pstmt.setLong(1, projectId);
                int affectedRows = pstmt.executeUpdate();

                isDeleted = affectedRows > 0;


            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new EntityException("Projekt blev ikke slettet, noget gik galt!", Alert.DANGER);
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlException) {
            throw new EntityException("Projekt blev ikke slettet, noget gik galt!", Alert.DANGER);
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
            try {
                connection.setAutoCommit(false);

                for (String member : selectedTeamMembers) {
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
            throw new EntityException("Kunne ikke finde team medlemmer.", Alert.DANGER);
        }

        return userInformationDtoList;
    }
    //TODO COMBINE IN QUERY
    @Override
    public List<UserRole> getAllUserRoles() {
        List<UserRole> roles = new ArrayList<>(2);
        String getAllUserRoles = """
                SELECT name
                FROM role
                WHERE name = 'PROJECT_ADMIN' OR name = 'PROJECT_MEMBER';
                """;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getAllUserRoles);
            ResultSet userRoles = pstmt.executeQuery();

            while (userRoles.next()) {
                roles.add(UserRole.valueOf(userRoles.getString(1)));
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return roles;
    }

    @Override
    public List<Status> getStatusList() {
        List<Status> statusList = new ArrayList<>();
        String selectQuery = """
                SELECT name FROM status;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                statusList.add(Status.valueOf(rs.getString(1)));
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return statusList;
    }


    @Override
    public UserEntityRoleDto getUserFromParentId(String username, long parentId) {
        UserEntityRoleDto user = null;

        try (Connection connection = dataSource.getConnection()) {
            String getUserFromParent = """
                    SELECT username, role_id, task_id, project_id, team_id, department_id, organisation_id
                    FROM user_entity_role
                    WHERE username = ? AND team_id = ?
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getUserFromParent);
            pstmt.setString(1, username);
            pstmt.setLong(2, parentId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String name = rs.getString(1);
                long roleId = rs.getLong(2);
                long taskId = rs.getLong(3);
                long projectId = rs.getLong(4);
                long teamId = rs.getLong(5);
                long deptId = rs.getLong(6);
                long orgId = rs.getLong(7);

                user = new UserEntityRoleDto(name, roleId, taskId, projectId,
                        teamId, deptId, orgId);
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return user;
    }

    @Override
    public void assignMemberToEntity(long projectId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.assignProjectRole(connection,projectId,
                    UserRole.PROJECT_MEMBER,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void promoteMemberToAdmin(long projectId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.removeProjectRole(connection,projectId,
                    UserRole.PROJECT_ADMIN,username);
            RoleAssignUtil.removeProjectRole(connection,projectId,
                    UserRole.PROJECT_MEMBER,username);
            RoleAssignUtil.assignProjectRole(connection,projectId,
                    UserRole.PROJECT_ADMIN,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void kickMember(long projectId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.removeProjectRole(connection,projectId,
                    UserRole.PROJECT_ADMIN,username);
            RoleAssignUtil.removeProjectRole(connection,projectId,
                    UserRole.PROJECT_MEMBER,username);
            RoleAssignUtil.removeProjectRole(connection,projectId,
                    UserRole.PROJECT_USER,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public List<UserEntityRoleDto> getUsersFromParentIdAndEntityId(long teamId, long projectId) {
        List<UserEntityRoleDto> users = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String getAllUsersFromTeam = """
                    SELECT DISTINCT username, role_id, task_id, project_id, team_id, department_id, organisation_id
                    FROM user_entity_role
                    WHERE team_id = ? OR project_id = ?
                    ORDER BY username;
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getAllUsersFromTeam);
            pstmt.setLong(1, teamId);
            pstmt.setLong(2, projectId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String username = rs.getString(1);
                long roleId = rs.getLong(2);
                long taskId = rs.getLong(3);
                long pId = rs.getLong(4); //changed because "projectId" is used in parameter
                long tId = rs.getLong(5); //changed name because "teamId" is used in parameter
                long deptId = rs.getLong(6);
                long orgId = rs.getLong(7);

                UserEntityRoleDto newUser = new UserEntityRoleDto(username, roleId, taskId, pId,
                        tId, deptId, orgId);

                users.add(newUser);
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }


        List<UserEntityRoleDto> cleanedUsers = getCleanUserEntityRoleDtos(users);

        return cleanedUsers;
    }


    private static List<UserEntityRoleDto> getCleanUserEntityRoleDtos(List<UserEntityRoleDto> users) {
        List<UserEntityRoleDto> cleanedUsers = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            if(i+1 != users.size()){ //avoids out of bounds
                if (users.get(i+1).username().equals(users.get(i).username())){
                    //adds user with the teamId of the duplicate that comes after it
                    cleanedUsers.add(new UserEntityRoleDto(users.get(i).username(),
                            users.get(i).roleId(), users.get(i).taskId(), users.get(i).projectId(),
                            users.get(i+1).teamId(), users.get(i).departmentId(),
                            users.get(i).organizationId()));
                }else if(i != 0){ //avoids out of bounds
                    if (!users.get(i-1).username().equals(users.get(i).username())){
                        cleanedUsers.add(users.get(i));
                    }
                }
            }else if(i != 0){
                if(!users.get(i-1).username().equals(users.get(i).username())) {
                    cleanedUsers.add(users.get(i));
                }
            }
        }
        return cleanedUsers;
    }
}
