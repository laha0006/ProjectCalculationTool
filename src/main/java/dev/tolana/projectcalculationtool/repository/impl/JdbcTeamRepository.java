package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.model.Team;
import dev.tolana.projectcalculationtool.repository.TeamRepository;
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
    public List<Entity> getAllEntitiesOnUsername(String username) {

        List<Entity> teams = new ArrayList<>();

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
                dateCreated = rs.getTimestamp(5).toLocalDateTime();
                archived = rs.getBoolean(6);
                department_id = rs.getLong(4);

                teams.add(new Team(id, name, description,dateCreated, archived, department_id));
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return teams;

    }

    @Override Entity getEntityOnId(long teamId){
        Entity team = null;
        try( Connection connection = datasource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM team WHERE id = ?");
            preparedStatement.setLong(1,teamId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                team = new Team(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getTimestamp(5).toLocalDateTime(),
                        resultSet.getBoolean(6)
                )
            }
        }
    }

    @Override
    public boolean createEntity(String username, Entity entity) {

        try (Connection connection = datasource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createTeam = "INSERT INTO team(name, description) VALUES (?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createTeam,
                        Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, entity.getName());
                pstmtAdd.setString(2, entity.getDescription());

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
        return true;
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
}
