package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.ResourceEntity;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.ResourceEntityCrudOperations;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class JdbcTaskRepository implements TaskRepository {

    private final DataSource dataSource;

    public JdbcTaskRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createEntity(String username, Entity task) {
        boolean isCreated;

        try (Connection connection = dataSource.getConnection()) {
            try {//query for task creation only concerns values that are not default
                connection.setAutoCommit(false);
                String createTask = isParentOrChildTask((ResourceEntity) task);
                PreparedStatement pstmt = connection.prepareStatement(createTask);
                setTaskAttributeValues(pstmt, (Task) task);

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

    private String isParentOrChildTask(ResourceEntity task) {
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

    private void setTaskAttributeValues(PreparedStatement pstmt, Task task) {
        try {
            if (task.getParentId() == 0) {
                pstmt.setString(1, task.getName());
                pstmt.setString(2, task.getDescription());
                pstmt.setLong(3, task.getProjectId());
                pstmt.setDate(4, Date.valueOf(task.getDeadline().toLocalDate()));
                pstmt.setInt(5, task.getEstimatedHours());
            } else {
                pstmt.setString(1, task.getName());
                pstmt.setString(2, task.getDescription());
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
    public Entity getEntityOnId(long taskId) {
        Task task = null;
        String getTaskOnId = """
                SELECT * FROM task
                WHERE id = ?
                """;

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getTaskOnId);
            pstmt.setLong(1, taskId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                task = new Task(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(11),
                        rs.getTimestamp(6).toLocalDateTime(),
                        Status.valueOf(rs.getString(9)),
                        rs.getLong(10),
                        rs.getLong(4),
                        rs.getInt(7),
                        rs.getInt(8)
                );
            }

        }catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return task;
    }

    @Override
    public List<Entity> getAllEntitiesOnUsername(String username) {
        return null;
    }

    @Override
    public List<Entity> getAllEntitiesOnId(long projectId) {
        List<Entity> taskList = new ArrayList<>();
        String retrieveAllTaskOnProjectId = """
                SELECT
                t.id,
                t.name,
                t.description,
                t.project_id,
                t.date_created,
                t.deadline,
                t.estimated_hours,
                t.actual_hours,
                s.name,
                t.parent_id,
                t.archived
                FROM task t
                LEFT JOIN status s ON t.status = s.id
                WHERE t.project_id = ? AND t.parent_id IS NULL
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(retrieveAllTaskOnProjectId);
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task retrivedTask = new Task(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(5).toLocalDateTime(),
                        rs.getBoolean(11),
                        rs.getTimestamp(6).toLocalDateTime(),
                        Status.valueOf(rs.getString(9)),
                        rs.getLong(10),
                        rs.getLong(4),
                        rs.getInt(7),
                        rs.getInt(8)
                );
                taskList.add(retrivedTask);
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return taskList;
    }

    @Override
    public List<Entity> getChildren(long parentId) {
        return null;
    }

    @Override
    public boolean editEntity(Entity entity) {
        return false;
    }

    @Override
    public boolean deleteEntity(long taskId) {
        boolean isDeleted;
        String deleteTask = """
                DELETE FROM task WHERE id = ?;
                """;

        try (Connection connection = dataSource.getConnection()){
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(deleteTask);
                pstmt.setLong(1, taskId);
                int affectedRows = pstmt.executeUpdate();

                isDeleted = affectedRows > 0;

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
        return isDeleted;
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

    @Override
    public boolean changeStatus(long resourceEntityId) {
        return false;
    }
}
