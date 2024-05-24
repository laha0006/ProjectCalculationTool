package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.model.*;
import dev.tolana.projectcalculationtool.repository.OrganisationRepository;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcOrganisationRepository implements OrganisationRepository {

    private DataSource dataSource;

    public JdbcOrganisationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createEntity(String username, Entity entity) {
        boolean isCreated = false;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createOrganisation = "INSERT INTO organisation(name, description) VALUES (?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createOrganisation, Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, entity.getName());
                pstmtAdd.setString(2, entity.getDescription());

                pstmtAdd.executeUpdate();
                ResultSet rs = pstmtAdd.getGeneratedKeys();

                long organisationId;
                if (rs.next()) {
                    organisationId = rs.getLong(1);
                    RoleAssignUtil.assignOrganisationRole(connection, organisationId, UserRole.ORGANISATION_OWNER, username);
                    isCreated = true;
                } else {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    throw new EntityException("Organisation blev ikke oprettet, noget gik galt!", Alert.WARNING);
                }

                connection.commit();
                connection.setAutoCommit(true);

            } catch (Exception exception) {
                connection.rollback();
                connection.setAutoCommit(true);
                if (exception instanceof DataTruncation) {
                    throw new EntityException("Organisation blev ikke oprettet, navn eller beskrivelse er for lang!", Alert.WARNING);
                }
                throw new EntityException("Organisation blev ikke oprettet, noget gik galt!", Alert.DANGER);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Organisation blev ikke oprettet, noget gik galt!", Alert.DANGER);
        }
        return isCreated;
    }

    @Override
    public Entity getEntityOnId(long organisationId) {
        Entity organisation = null;
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM organisation WHERE id = ?");
            pstmt.setLong(1, organisationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                organisation = new Organisation(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(4).toLocalDateTime(),
                        rs.getBoolean(5)
                );
            } else {
                throw new EntityException("Organisation findes ikke!", Alert.WARNING);
            }
        } catch (SQLException e) {
//            throw new RuntimeException(e);
            throw new EntityException("Organisation findes ikke!", Alert.WARNING);
        }
        return organisation;
    }

    @Override
    public List<Entity> getAllEntitiesOnUsername(String username) {
        List<Entity> organisations = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String getAllOrganisations = """
                    SELECT organisation.id, name, description, date_created, archived 
                    FROM organisation 
                    JOIN user_entity_role ON organisation.id = user_entity_role.organisation_id
                    JOIN users ON user_entity_role.username = users.username
                    WHERE users.username = ?
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getAllOrganisations);
            pstmt.setString(1, username);


            ResultSet rs = pstmt.executeQuery();

            long id;
            String name;
            String description;
            LocalDateTime dateCreated;
            boolean archived;


            while (rs.next()) {

                id = rs.getLong(1);
                name = rs.getString(2);
                description = rs.getString(3);
                dateCreated = rs.getTimestamp(4).toLocalDateTime();
                archived = rs.getBoolean(5);

                organisations.add(new Organisation(id, name, description, dateCreated, archived));
            }


        } catch (SQLException sqlException) {
            throw new EntityException("Noget gik galt! Kunne ikke hente organisaiont(er)", Alert.WARNING);
        }

        return organisations;
    }

    @Override
    public List<Entity> getAllEntitiesOnId(long entityId) {
        return null;
    }

    @Override
    public List<Entity> getChildren(long organisationId) {
        List<Entity> departmentList = new ArrayList<>();
        String getAllTeamsFromParent = """
                SELECT * FROM department
                WHERE organisation_id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getAllTeamsFromParent);
            pstmt.setLong(1, organisationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Department department = new Department(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(6),
                        rs.getLong(4)
                );
                departmentList.add(department);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Noget gik galt! Kunne ikke hente afdeling(er).", Alert.WARNING);
        }

        return departmentList;
    }

    @Override
    public Entity getParent(long parentId) {
        return null;
    }

    @Override
    public boolean editEntity(Entity entity) {
        boolean isEdited;
        String editOrganisation = """
                UPDATE organisation
                SET name = ?, description = ?
                WHERE id = ?;
                """;
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(editOrganisation);
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
                throw new EntityException("Noget gik galt! Kunne ikke opdatere organisation.", Alert.DANGER);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Noget gik galt! Kunne ikke opdatere organisation.", Alert.DANGER);
        }
        return isEdited;
    }

    @Override
    public boolean deleteEntity(long organisationId) {
        boolean isDeleted;
        String deleteOrganisation = """
                DELETE FROM organisation WHERE id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(deleteOrganisation);
                pstmt.setLong(1, organisationId);
                int affectedRows = pstmt.executeUpdate();

                isDeleted = affectedRows > 0;

                connection.commit();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new EntityException("Kunne ikke slette organisation!", Alert.DANGER);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Kunne ikke slette organisation!", Alert.DANGER);
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
    public List<UserInformationDto> getUsersFromEntityId(long entityId) {
        return null;
    }

    @Override
    public List<UserEntityRoleDto> getUsersFromOrganisationId(long entityId) {
        List<UserEntityRoleDto> users = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String getAllUsersFromOrganisation = """
                    SELECT username, role_id, task_id, project_id, team_id, department_id, organisation_id
                    FROM user_entity_role
                    WHERE organisation_id = ?
                    ORDER BY username;
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getAllUsersFromOrganisation);
            pstmt.setLong(1, entityId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String username = rs.getString(1);
                long roleId = rs.getLong(2);
                long taskId = rs.getLong(3);
                long projectId = rs.getLong(4);
                long teamId = rs.getLong(5);
                long deptId = rs.getLong(6);
                long orgId = rs.getLong(7);

                users.add(new UserEntityRoleDto(username, roleId, taskId, projectId,
                        teamId, deptId, orgId));
            }


        } catch (SQLException sqlException) {
            throw new EntityException("Kunne ikke hente organisations medlemmer.", Alert.WARNING);
        }

        return users;
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return null;
    }

    @Override
    public List<Invitation> getAllOutstandingInvitations(long organisationId) {
        List<Invitation> outstandingInvitations = new ArrayList<>();
        String SQL = "SELECT * FROM invitation WHERE organisation_iu = ?";
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setLong(1, organisationId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                outstandingInvitations.add(new Invitation(
                        rs.getString(1),
                        rs.getLong(2)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return outstandingInvitations;
    }


    @Override
    public UserEntityRoleDto getUserFromOrganisationId(String username, long organisationId) {
        UserEntityRoleDto user = null;

        try (Connection connection = dataSource.getConnection()) {
            String getAllUsersFromOrganisation = """
                    SELECT username, role_id, task_id, project_id, team_id, department_id, organisation_id
                    FROM user_entity_role
                    WHERE username = ? AND organisation_id = ?
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getAllUsersFromOrganisation);
            pstmt.setString(1, username);
            pstmt.setLong(2, organisationId);

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
    public void promoteMemberToAdmin(long orgId, String username){
        try (Connection connection = dataSource.getConnection()) {
            //in order to avoid getting multiple admin entries from several presses
            //we start up by removing admin
            RoleAssignUtil.removeOrganisationRole(connection,orgId,
                    UserRole.ORGANISATION_ADMIN,username);
            //then we removed member, before adding admin
            //TODO ideally, we want the option to add admin rank to only show up if the user
            // isn't an admin already, but this works for now
            RoleAssignUtil.removeOrganisationRole(connection,orgId,
                    UserRole.ORGANISATION_MEMBER,username);
            RoleAssignUtil.assignOrganisationRole(connection,orgId,
                    UserRole.ORGANISATION_ADMIN,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void kickMember(long orgId, String username){
        //TODO
        // add "remove from all" into this, such that it recursively removes a
        // member from dept, team, project, task
        try (Connection connection = dataSource.getConnection()) {
            RoleAssignUtil.removeOrganisationRole(connection,orgId,
                    UserRole.ORGANISATION_ADMIN,username);
            RoleAssignUtil.removeOrganisationRole(connection,orgId,
                    UserRole.ORGANISATION_MEMBER,username);
            RoleAssignUtil.removeOrganisationRole(connection,orgId,
                    UserRole.ORGANISATION_USER,username);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public UserEntityRoleDto getUserFromParentId(String username, long parentId) {
        return null;
    }

    @Override
    public void assignMemberToEntity(long entityId, String username) {

    }

    @Override
    public List<UserEntityRoleDto> getUsersFromParentIdAndEntityId(long parentId, long entityId) {
        return null;
    }
}
