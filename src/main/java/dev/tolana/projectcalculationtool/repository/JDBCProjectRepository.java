package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Project;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JDBCProjectRepository implements ProjectRepository {

    private DataSource dataSource;

    public JDBCProjectRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int addProject(Project project){
        int projectId = -1;

        try (Connection connection = dataSource.getConnection()) {
            String insertNewProject = "INSERT INTO project (name, description, team_id," +
                                      "allotted_hours, status) " +
                                      "VALUES (?,?,?,?,?);";

            PreparedStatement pstmt = connection.prepareStatement(insertNewProject,
                    Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setLong(3, project.getTeamId());
            pstmt.setLong(4, project.getAllottedHours());
            pstmt.setInt(5, project.getStatus());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                projectId = generatedKeys.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return projectId;
    }




}
