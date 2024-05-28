package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.model.Team;
import dev.tolana.projectcalculationtool.repository.DepartmentRepository;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class JdbcDepartmentRepository implements DepartmentRepository {

    private DataSource dataSource;

    public JdbcDepartmentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createEntity(String username, Entity entity) {
        boolean isCreated;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createDepartment = "INSERT INTO department(name, description, organisation_id) VALUES (?, ?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createDepartment,
                        Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, entity.getName());
                pstmtAdd.setString(2, entity.getDescription());
                pstmtAdd.setLong(3, ((Department) entity).getOrganisationId());
                int affectedRows = pstmtAdd.executeUpdate();
                isCreated = affectedRows > 0;

                ResultSet rs = pstmtAdd.getGeneratedKeys();
                if (rs.next()) {
                    long departmentId = rs.getLong(1);
                    RoleAssignUtil.assignDepartmentRole(connection, departmentId,
                            UserRole.DEPARTMENT_OWNER, username);
                } else {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    throw new EntityException("Afdeling blev ikke oprettet, noget gik galt!", Alert.DANGER);
                }

            } catch (Exception exception) {
                connection.rollback();
                connection.setAutoCommit(true);
                if (exception instanceof DataTruncation) {
                    throw new EntityException("Afdeling blev ikke oprettet, navn eller beskrivelse er for lang!", Alert.WARNING);
                }
                throw new EntityException("Afdeling blev ikke oprettet, noget gik galt!", Alert.DANGER);
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlException) {
            throw new EntityException("Afdeling blev ikke oprettet, noget gik galt!", Alert.DANGER);
        }
        return isCreated;
    }

    @Override
    public Entity getEntityOnId(long deptId) {
        Entity department;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM department WHERE id = ?");
            preparedStatement.setLong(1, deptId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                department = new Department(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getTimestamp(5).toLocalDateTime(),
                        resultSet.getBoolean(6),
                        resultSet.getLong(4)
                );
            } else {
                throw new EntityException("Afdeling findes ikke!", Alert.WARNING);
            }
        } catch (SQLException e) {
            throw new EntityException("Afdeling findes ikke!", Alert.WARNING);
        }
        return department;
    }

    @Override
    public List<Entity> getAllEntitiesOnUsername(String username) {
        return null;
    }

    @Override
    public List<Entity> getChildren(long departmentId) {
        List<Entity> teamList = new ArrayList<>();
        String getAllTeamsFromParent = """
                SELECT * FROM team
                WHERE department_id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getAllTeamsFromParent);
            pstmt.setLong(1, departmentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Team team = new Team(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(6),
                        rs.getLong(4)
                );
                teamList.add(team);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Kunne ikke hente teams", Alert.WARNING);

        }

        return teamList;
    }

    @Override
    public Entity getParent(long parentId) {
        Entity parent;
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM organisation WHERE id = ?");
            pstmt.setLong(1, parentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                parent = new Organisation(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(4).toLocalDateTime(),
                        rs.getBoolean(5)
                );
            } else {
                throw new EntityException("Kunne ikke finde organisation", Alert.WARNING);
            }
        } catch (SQLException e) {
            throw new EntityException("Kunne ikke finde organisation", Alert.WARNING);
        }
        return parent;
    }

    @Override
    public boolean editEntity(Entity entity) {
        boolean isEdited;
        String editDepartment = """
                UPDATE department
                SET name = ?, description = ?
                WHERE id = ?;
                """;
        try (Connection connection = dataSource.getConnection()){
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(editDepartment);
                pstmt.setString(1, entity.getName());
                pstmt.setString(2, entity.getDescription());
                pstmt.setLong(3, entity.getId());
                int affectedRows = pstmt.executeUpdate();

                isEdited = affectedRows > 0;

                connection.commit();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new RuntimeException(sqlException);
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return isEdited;
    }

    @Override
    public boolean deleteEntity(long departmentId) {
        boolean isDeleted;
        String deleteTask = """
                DELETE FROM department WHERE id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(deleteTask);
                pstmt.setLong(1, departmentId);
                int affectedRows = pstmt.executeUpdate();

                isDeleted = affectedRows > 0;

            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new EntityException("Afdeling blev ikke slettet! Noget gik galt.", Alert.DANGER);
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlException) {
            throw new EntityException("Afdeling blev ikke slettet! Noget gik galt.", Alert.DANGER);
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
    public List<UserEntityRoleDto> getUsersFromParentIdAndEntityId(long organisationId, long departmentId) {
        List<UserEntityRoleDto> users = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String getAllUsersFromOrganisation = """
                    SELECT DISTINCT username, role_id, task_id, project_id, team_id, department_id, organisation_id
                    FROM user_entity_role
                    WHERE organisation_id = ? OR department_id = ?
                    ORDER BY role_id DESC;
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getAllUsersFromOrganisation);
            pstmt.setLong(1, organisationId);
            pstmt.setLong(2, departmentId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String username = rs.getString(1);
                long roleId = rs.getLong(2);
                long taskId = rs.getLong(3);
                long projectId = rs.getLong(4);
                long teamId = rs.getLong(5);
                long deptId = rs.getLong(6);
                long orgId = rs.getLong(7);

                UserEntityRoleDto newUser = new UserEntityRoleDto(username, roleId, taskId, projectId,
                        teamId, deptId, orgId);

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
                    WHERE username = ? AND organisation_id = ?
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

                return user;
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return user;
    }

    @Override
    public void assignMemberToEntity(long deptId, String username){
        try (Connection connection = dataSource.getConnection()) {
           RoleAssignUtil.assignDepartmentRole(connection,deptId,
                   UserRole.DEPARTMENT_MEMBER,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void promoteMemberToAdmin(long deptId, String username){
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.removeDepartmentRole(connection,deptId,
                    UserRole.DEPARTMENT_ADMIN,username);
            RoleAssignUtil.removeDepartmentRole(connection,deptId,
                    UserRole.DEPARTMENT_MEMBER,username);
            RoleAssignUtil.assignDepartmentRole(connection,deptId,
                    UserRole.DEPARTMENT_ADMIN,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void kickMember(long deptId, String username){
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.removeAllRoles(connection, EntityType.DEPARTMENT,deptId,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
