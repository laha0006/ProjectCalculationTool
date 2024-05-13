package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcTaskRepository implements TaskRepository {

    private DataSource dataSource;

    public JdbcTaskRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public boolean createParentTask(Task task, String username) {
        boolean isCreated = false;

        try (Connection connection = dataSource.getConnection()) {
            try {//query for task creation only concerns values that are not default
                connection.setAutoCommit(false);
                String createTask = """
                        INSERT INTO task (name, description, project_id, deadline, estimated_hours) VALUES (?,?,?,?,?);
                        """;
                PreparedStatement pstmt = connection.prepareStatement(createTask);
                pstmt.setString(1, task.getTaskName());
                pstmt.setString(2, task.getTaskDescription());
                pstmt.setLong(3, task.getProjectId());
                pstmt.setDate(4, Date.valueOf(task.getDeadline()));
                pstmt.setInt(5, task.getEstimatedHours());
                int affectedRows = pstmt.executeUpdate();

                isCreated = affectedRows > 0;
                connection.commit();
                connection.setAutoCommit(true);

            }catch (Exception exception) {
                connection.rollback();
                connection.setAutoCommit(true);
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
//TODO WHEN TASK GETS CREATED IT IS BY DEFAULT UNASSIGEN A PROJECT MEMBER, THEREFORE CREATE METHOD WHERE MEMBER GET ASSIGEND A TASK TOO AND NOT ONLY A PROJECT
        return isCreated;
    }

    @Override
    public boolean deleteTask(long taskId, String username) {
        return false;
    }

    @Override
    public boolean editTask(long taskId, String username) {
        return false;
    }

    @Override
    public Task getTask(long taskId, String username) {
        return null;
    }

    @Override
    public List<Task> getAllTasks(String username) {
        return null;
    }

    @Override
    public List<Task> getAllProjectTasks(long projectId, String username) {
        List<Task> taskList = new ArrayList<>();
        String retrieveAllTaskOnProjectId = """
                SELECT * FROM task t
                JOIN user_entiry_role uer ON uer.project_id = t.project_id
                WHERE t.project_id = ? AND uer.username = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(retrieveAllTaskOnProjectId);
            pstmt.setLong(1, projectId);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task retrivedTask = new Task(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getLong(4),
                        rs.getTimestamp(5).toLocalDateTime(), //TODO figure out time attribute
                        rs.getTimestamp(6).toLocalDateTime(),
                        rs.getInt(7),
                        rs.getInt(8),
                        rs.getLong(9),
                        rs.getBoolean(10)

                );
                taskList.add(retrivedTask);
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return taskList;
    }
}
