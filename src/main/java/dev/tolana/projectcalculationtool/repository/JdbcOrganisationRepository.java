package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Organisation;
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


                long organisationId = -1;
                if (rs.next()) {
                    organisationId = rs.getLong(1);
                }
                String assignOrganisationToUser = "INSERT INTO user_entity_role(username, role_id, organisation_id) VALUES (?, ?, ?)";
                PreparedStatement pstmtAssign = connection.prepareStatement(assignOrganisationToUser);
                pstmtAssign.setString(1, username);
                pstmtAssign.setLong(2, 1);
                pstmtAssign.setLong(3, organisationId);
                pstmtAssign.executeUpdate();

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

}
