package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Invitation;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.repository.OrganisationRepository;
import dev.tolana.projectcalculationtool.util.RoleUtil;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcOrganisationRepository implements OrganisationRepository {

    private DataSource datasource;

    public JdbcOrganisationRepository(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public boolean createEntity(String username, Entity entity) {
        boolean isCreated = false;

        try (Connection connection = datasource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createOrganisation = "INSERT INTO organisation(name, description) VALUES (?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createOrganisation, Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, entity.getName());
                pstmtAdd.setString(2, entity.getDescription());

                pstmtAdd.executeUpdate();
                ResultSet rs = pstmtAdd.getGeneratedKeys();

                long organisationId = 0;
                if (rs.next()) {
                    organisationId = rs.getLong(1);
                    RoleUtil.assignOrganisationRole(connection, organisationId, UserRole.ORGANISATION_OWNER, username);
                    isCreated = true;
                }
//                String assignOrganisationToUser = "INSERT INTO user_entity_role(username, role_id, organisation_id) VALUES (?, ?, ?)";
//                PreparedStatement pstmtAssign = connection.prepareStatement(assignOrganisationToUser);
//                pstmtAssign.setString(1, username);
//                pstmtAssign.setLong(2, 1);
//                pstmtAssign.setLong(3, organisationId);
//                int affectedRows = pstmtAssign.executeUpdate();
//                isCreated = affectedRows > 0;


                connection.commit();
                connection.setAutoCommit(true);

            } catch (Exception exception) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return isCreated;
    }

    @Override
    public Entity getEntityOnId(long organisationId) {
        Entity organisation = null;
        try(Connection con = datasource.getConnection()) {
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
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return organisation;
    }

    @Override
    public List<Entity> getAllEntitiesOnUsername(String username) {
        List<Entity> organisations = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
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
            sqlException.printStackTrace();
        }

        return organisations;
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
    public boolean assignUser(long entityId, List<String> username, UserRole role) {
        return false;
    }

    @Override
    public List<UserInformationDto> getUsersFromEntityId(long entityId) {
        return null;
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return null;
    }

    @Override
    public List<Invitation> getAllOutstandingInvitations(long organisationId) {
        List<Invitation> outstandingInvitations = new ArrayList<>();
        String SQL = "SELECT * FROM invitation WHERE organisation_iu = ?";
        try(Connection con = datasource.getConnection() ) {
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
}
