package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class JdbcTaskRepository implements TaskRepository {

    private DataSource dataSource;

    public JdbcTaskRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public boolean createParentTask(Task task , String username) {
        boolean isCreated = false;

        try (Connection connection = dataSource.getConnection()) {
            //query for task creation only concerns values that are not default
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

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

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
        return null;
    }
}
