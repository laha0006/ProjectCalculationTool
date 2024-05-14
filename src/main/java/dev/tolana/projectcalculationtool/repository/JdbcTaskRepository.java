package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcTaskRepository implements TaskRepository {

    private final DataSource dataSource;

    public JdbcTaskRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String isParentOrChildTask(Task task) {
        String sql;
        if (task.getParentId() == 0) {
            sql = """
                    INSERT INTO task (name, description, project_id, deadline, estimated_hours) VALUES (?,?,?,?,?);
                    """;
        } else {
            sql = """
                    INSERT INTO task (name, description, project_id, deadline, estimated_hours, parent_id) VALUES (?, ?, ?, ?, ?, ?);
                    """;
        }

        return sql;
    }

    @Override
    public boolean createTask(Task task, String username) {
        boolean isCreated;

        try (Connection connection = dataSource.getConnection()) {
            try {//query for task creation only concerns values that are not default
                connection.setAutoCommit(false);
                String createTask = isParentOrChildTask(task);
                PreparedStatement pstmt = connection.prepareStatement(createTask);
                setTaskAttributeValues(pstmt, task);

                int affectedRows = pstmt.executeUpdate();
                isCreated = affectedRows > 0;
                connection.commit();
                connection.setAutoCommit(true);

            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new RuntimeException(sqlException);
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
//TODO WHEN TASK GETS CREATED IT IS BY DEFAULT UNASSIGEN A PROJECT MEMBER, THEREFORE CREATE METHOD WHERE MEMBER GET ASSIGEND A TASK TOO AND NOT ONLY A PROJECT
        return isCreated;
    }

    private void setTaskAttributeValues(PreparedStatement pstmt, Task task) {
        try {
            if (task.getParentId() == 0) {
                pstmt.setString(1, task.getTaskName());
                pstmt.setString(2, task.getTaskDescription());
                pstmt.setLong(3, task.getProjectId());
                pstmt.setDate(4, Date.valueOf(task.getDeadline().toLocalDate()));
                pstmt.setInt(5, task.getEstimatedHours());
            } else {
                pstmt.setString(1, task.getTaskName());
                pstmt.setString(2, task.getTaskDescription());
                pstmt.setLong(3, task.getProjectId());
                pstmt.setDate(4, Date.valueOf(task.getDeadline().toLocalDate()));
                pstmt.setInt(5, task.getEstimatedHours());
                pstmt.setLong(6, task.getParentId());
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
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
                JOIN user_entity_role uer ON uer.project_id = t.project_id
                WHERE t.project_id = ? AND uer.username = ? AND t.parent_id IS NULL;
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
                        rs.getTimestamp(5).toLocalDateTime(),
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
