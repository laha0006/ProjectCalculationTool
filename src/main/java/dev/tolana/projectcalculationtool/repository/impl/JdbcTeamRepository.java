package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.model.*;
import dev.tolana.projectcalculationtool.repository.TeamRepository;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcTeamRepository implements TeamRepository {

    private DataSource dataSource;

    public JdbcTeamRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //OBSOLETE!
    @Override
    public List<Entity> getAllEntitiesOnUsername(String username) {

        List<Entity> teams = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String getAllTeams = """
                    SELECT team.id, name, description, date_created, archived
                    FROM team
                    JOIN user_entity_role ON team.id = user_entity_role.team_id
                    JOIN users ON user_entity_role.username = users.username 
                    WHERE users.username = ?;
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getAllTeams);
            pstmt.setString(1, username);


            ResultSet rs = pstmt.executeQuery();

            long id;
            String name;
            String description;
            long department_id;
            LocalDateTime dateCreated;
            boolean archived;


            while (rs.next()) {

                id = rs.getLong(1);
                name = rs.getString(2);
                description = rs.getString(3);
                dateCreated = rs.getTimestamp(5).toLocalDateTime();
                archived = rs.getBoolean(6);
                department_id = rs.getLong(4);

                teams.add(new Team(id, name, description, dateCreated, archived, department_id));
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return teams;

    }

    @Override
    public Entity getEntityOnId(long deptId) {
        Entity team = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM team WHERE id = ?");
            preparedStatement.setLong(1, deptId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                team = new Team(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getTimestamp(5).toLocalDateTime(),
                        resultSet.getBoolean(6),
                        resultSet.getLong(4)
                );
            }
        } catch (SQLException e) {
            throw new EntityException("Team blev ikke fundet, noget gik galt!", Alert.WARNING);
        }
        return team;
    }

    @Override
    public List<Entity> getChildren(long teamId) {
        List<Entity> projectList = new ArrayList<>();
        String getAllTeamsFromParent = """
                SELECT
                p.id,
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
                LEFT JOIN status s ON p.status = s.id
                WHERE p.team_id = ? AND p.parent_id IS NULL
                """;
//        SELECT * FROM project
//        WHERE team_id = ?;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getAllTeamsFromParent);
            pstmt.setLong(1, teamId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Project project = new Project(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(10),
                        rs.getTimestamp(6).toLocalDateTime(),
                        Status.valueOf(rs.getString(8)),
                        -1,
                        rs.getLong(4),
                        rs.getInt(7)
                );
                projectList.add(project);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Kunne ikke hente projekter, noget gik galt!", Alert.WARNING);
        }

        return projectList;
    }

    @Override
    public Entity getParent(long parentId) {
        Entity parent;
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM department WHERE id = ?");
            pstmt.setLong(1, parentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                parent = new Department(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(6),
                        rs.getLong(4)
                );
            } else {
                throw new EntityException("Kunne ikke finde afdeling", Alert.WARNING);
            }
        } catch (SQLException e) {
            throw new EntityException("Kunne ikke finde afdeling", Alert.WARNING);
        }
        return parent;
    }

    @Override
    public boolean createEntity(String username, Entity entity) {
        boolean isCreated = false;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createTeam = "INSERT INTO team(name, description, department_id) VALUES (?, ?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createTeam,
                        Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, entity.getName());
                pstmtAdd.setString(2, entity.getDescription());
                pstmtAdd.setLong(3, ((Team) entity).getDepartmentId());
                int affectedRows = pstmtAdd.executeUpdate();
                isCreated = affectedRows > 0;

                ResultSet rs = pstmtAdd.getGeneratedKeys();
                if (rs.next()) {
                    long teamId = rs.getLong(1);
                    RoleAssignUtil.assignTeamRole(connection, teamId,
                            UserRole.TEAM_OWNER, username);
                } else {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    throw new EntityException("Team blev ikke oprettet, noget gik galt!", Alert.DANGER);
                }


            } catch (Exception exception) {
                connection.rollback();
                connection.setAutoCommit(true);
                if (exception instanceof DataTruncation) {
                    throw new EntityException("Team blev ikke oprettet, navn eller beskrivelse for lang!", Alert.WARNING);
                }
                throw new EntityException("Team blev ikke oprettet, noget gik galt!", Alert.DANGER);
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlException) {
            throw new EntityException("Team blev ikke oprettet, noget gik galt!", Alert.DANGER);
        }
        return isCreated;
    }


    @Override
    public boolean editEntity(Entity entity) {
        String SQL = """
                UPDATE team SET name = ?, description = ? WHERE id = ?
                """;
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getDescription());
            pstmt.setLong(3, entity.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new EntityException("Team blev ikke opdateret, noget gik galt!", Alert.DANGER);
        }
    }

    @Override
    public boolean deleteEntity(long teamId) {
        boolean isDeleted;
        String deleteTask = """
                DELETE FROM team WHERE id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(deleteTask);
                pstmt.setLong(1, teamId);
                int affectedRows = pstmt.executeUpdate();
                isDeleted = affectedRows > 0;

            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new EntityException("Team blev ikke slettet, noget gik galt!", Alert.DANGER);
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlException) {
            throw new EntityException("Team blev ikke slettet, noget gik galt!", Alert.DANGER);
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
    public boolean assignUser(long entityId, List<String> username, UserRole role) {
        return false;
    }

    @Override
    public List<UserEntityRoleDto> getUsersFromEntityId(long entityId) {
        return null;
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return null;
    }

    @Override
    public List<UserEntityRoleDto> getUsersFromParentIdAndEntityId(long departmentId, long teamId) {
        List<UserEntityRoleDto> users = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String getAllUsersFromDepartment = """
                    SELECT DISTINCT username, role_id, task_id, project_id, team_id, department_id, organisation_id
                    FROM user_entity_role
                    WHERE department_id = ? OR team_id = ?
                    ORDER BY role_id DESC;
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getAllUsersFromDepartment);
            pstmt.setLong(1, departmentId);
            pstmt.setLong(2, teamId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String username = rs.getString(1);
                long roleId = rs.getLong(2);
                long taskId = rs.getLong(3);
                long projectId = rs.getLong(4);
                long tId = rs.getLong(5); //changed name because "teamId" is used in parameter
                long deptId = rs.getLong(6);
                long orgId = rs.getLong(7);

                UserEntityRoleDto newUser = new UserEntityRoleDto(username, roleId, taskId, projectId,
                        tId, deptId, orgId);

                users.add(newUser);
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return users;
    }

    @Override
    public UserEntityRoleDto getUserFromParentId(String username, long parentId) {
        UserEntityRoleDto user = null;

        try (Connection connection = dataSource.getConnection()) {
            String getUserFromParent = """
                    SELECT username, role_id, task_id, project_id, team_id, department_id, organisation_id
                    FROM user_entity_role
                    WHERE username = ? AND department_id = ?
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
    public void assignMemberToEntity(long teamId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.assignTeamRole(connection,teamId,
                    UserRole.TEAM_MEMBER,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void promoteMemberToAdmin(long teamId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.removeTeamRole(connection,teamId,
                    UserRole.TEAM_ADMIN,username);
            RoleAssignUtil.removeTeamRole(connection,teamId,
                    UserRole.TEAM_MEMBER,username);
            RoleAssignUtil.assignTeamRole(connection,teamId,
                    UserRole.TEAM_ADMIN,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void kickMember(long teamId, String username) {
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.removeAllRoles(connection, EntityType.TEAM,teamId,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
