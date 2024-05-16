package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
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
    public List<Organisation> getOrganisationsByUser(String username) {

        List<Organisation> organisations = new ArrayList<>();

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
    public void createOrganisation(String username, String organisationName, String organisationDescription) {

        try (Connection connection = datasource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createOrganisation = "INSERT INTO organisation(name, description) VALUES (?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createOrganisation, Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, organisationName);
                pstmtAdd.setString(2, organisationDescription);
                pstmtAdd.executeUpdate();
                ResultSet rs = pstmtAdd.getGeneratedKeys();

                if (rs.next()) {
                    long organisationId = rs.getLong(1);
                    RoleAssignUtil.assignOrganisationRole(connection, organisationId, UserRole.ORGANISATION_OWNER, username);
                }


                connection.commit();
                connection.setAutoCommit(true);

            } catch (Exception exception) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    @Override
    public Organisation getOrganisationById(long organisationId) {
        Organisation organisation = null;
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

}
