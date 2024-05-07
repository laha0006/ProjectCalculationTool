package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Organisation;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JDBCOrganisationRepository implements OrganisationRepository {

    private DataSource datasource;

    public JDBCOrganisationRepository(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public List<Organisation> getOrganisationsByUser(String username) {

        List<Organisation> organisations = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
            String getAllOrganisations = """
                    SELECT id, name, description, date_created, archived 
                    FROM organisation 
                    JOIN user_entity_role ON organisation.id = user_entity_role.organisation_id
                    JOIN users ON user_entity.username = users.username
                    WHERE username == ?
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
            String createOrganisation = "INSERT INTO organisation(name, description) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(createOrganisation);
            pstmt.setString(1, organisationName);
            pstmt.setString(2, organisationDescription);
            //pstmt.setString(3, username);

            pstmt.executeUpdate();


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

}
