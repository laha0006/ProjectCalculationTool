package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.model.Team;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcTeamRepository implements TeamRepository {

    private DataSource datasource;

    public JdbcTeamRepository(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public List<Team> getTeamsByUser(String username) {

        List<Team> teams = new ArrayList<>();

        try (Connection connection = datasource.getConnection()) {
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
                department_id = rs.getLong(4);
                dateCreated = rs.getTimestamp(5).toLocalDateTime();
                archived = rs.getBoolean(6);

                teams.add(new Team(id, name, description, department_id,dateCreated, archived));
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return teams;

    }

    @Override
    public void createTeam(String username, String teamName, String teamDescription) {

        try (Connection connection = datasource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createTeam = "INSERT INTO team(name, description) VALUES (?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createTeam,
                        Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, teamName);
                pstmtAdd.setString(2, teamDescription);

                pstmtAdd.executeUpdate();
                ResultSet rs = pstmtAdd.getGeneratedKeys();


                long teamId = -1;
                if (rs.next()) {
                    teamId = rs.getLong(1);
                }
                String assignTeamToUser =
                        "INSERT INTO user_entity_role(username, role_id, team_id) VALUES (?, ?, ?)";
                PreparedStatement pstmtAssign = connection.prepareStatement(assignTeamToUser);
                pstmtAssign.setString(1, username);
                pstmtAssign.setLong(2, 1);
                pstmtAssign.setLong(3, teamId);
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
