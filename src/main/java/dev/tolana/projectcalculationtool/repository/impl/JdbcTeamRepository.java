package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.model.Team;
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

                teams.add(new Team(id, name, description,dateCreated, archived, department_id));
            }


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return teams;

    }

    @Override
    public Entity getEntityOnId(long deptId) {
        Entity department = null;
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
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return department;
    }

    @Override
    public List<Entity> getAllEntitiesOnId(long departmentId) {
        List<Entity> teams = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM department WHERE department_id = ?");
            preparedStatement.setLong(1, departmentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                teams.add(new Team(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getTimestamp(5).toLocalDateTime(),
                        resultSet.getBoolean(6),
                        resultSet.getLong(4)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teams;
    }

    @Override
    public List<Entity> getChildren(long teamId) {
        List<Entity> projectList = new ArrayList<>();
        String getAllTeamsFromParent = """
                SELECT * FROM project
                WHERE team_id = ?;
                """;

        try (Connection connection = dataSource.getConnection()){
            PreparedStatement pstmt = connection.prepareStatement(getAllTeamsFromParent);
            pstmt.setLong(1, teamId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Project project = new Project(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(4).toLocalDateTime(),
                        rs.getBoolean(10),
                        rs.getTimestamp(6).toLocalDateTime(),
                        Status.valueOf(rs.getString(8)),
                        rs.getLong(9),
                        rs.getLong(4),
                        rs.getInt(7)
                );
                projectList.add(project);
            }
        }catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return projectList;
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
                pstmtAdd.setLong(3, ((Team)entity).getDepartmentId());
                int affectedRows = pstmtAdd.executeUpdate();
                isCreated = affectedRows > 0;

                ResultSet rs = pstmtAdd.getGeneratedKeys();
                if(rs.next()) {
                    long teamId = rs.getLong(1);
                    RoleAssignUtil.assignDepartmentRole(connection, teamId,
                            UserRole.TEAM_OWNER, username);
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
        return isCreated;
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
