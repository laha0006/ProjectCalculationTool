package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Organization;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JDBCOrganizationRepository implements OrganizationRepository {

    private DataSource datasource;

    public JDBCOrganizationRepository(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public List<Organization> getOrganizationsByUser(String username) {

        List<Organization> organizations = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
            String getAllOrganizations = """
                    SELECT id, name, description, date_created, archived 
                    FROM organization 
                    JOIN user_entity_role ON organization.id = user_entity_role.organization_id
                    JOIN users ON user_entity.username = users.username
                    WHERE username == ?
                    """;

            PreparedStatement pstmt = connection.prepareStatement(getAllOrganizations);
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

                organizations.add(new Organization(id, name, description, dateCreated, archived));
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return organizations;

    }

}
