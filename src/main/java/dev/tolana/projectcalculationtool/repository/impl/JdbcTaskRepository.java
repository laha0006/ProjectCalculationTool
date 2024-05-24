package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.ResourceEntity;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
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

    @Override
    public boolean createEntity(String username, Entity task) {
        boolean isCreated;

        try (Connection connection = dataSource.getConnection()) {
            try {//query for task creation only concerns values that are not default
                connection.setAutoCommit(false);
                String createTask = determineCreationQuery((ResourceEntity) task);
                PreparedStatement pstmt = connection.prepareStatement(createTask, Statement.RETURN_GENERATED_KEYS);
                setTaskAttributeValues(pstmt, (Task) task);

                int affectedRows = pstmt.executeUpdate();
                isCreated = affectedRows > 0;

                ResultSet generatedKey = pstmt.getGeneratedKeys();
                if (generatedKey.next()){
                    long taskId = generatedKey.getLong(1);
                    RoleAssignUtil.assignTaskRole(connection, taskId, UserRole.TASK_OWNER, username);

                } else {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    throw new EntityException("Opgaven blev ikke oprettet, noget gik galt!", Alert.DANGER);
                }

                connection.commit();
                connection.setAutoCommit(true);

            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                if (sqlException instanceof DataTruncation) {
                    throw new EntityException("Opgave blev ikke oprettet, navn eller beskrivelse er for lang!", Alert.WARNING);
                }
                throw new EntityException("Opgave blev ikke oprettet, noget gik galt!", Alert.DANGER);
            }

        } catch (SQLException sqlException) {
            throw new EntityException("Opgave blev ikke oprettet, noget gik galt!", Alert.DANGER);
        }
        return isCreated;
    }

    private String determineCreationQuery(ResourceEntity task) {
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
    public Entity getParent(long parentId) {
        return null;
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
        Task task;
        String getTaskOnId = """
                SELECT t.id,
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
                     LEFT JOIN status s
                               ON t.status = s.id
                WHERE t.id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
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
            } else {
                throw new EntityException("Opgave blev ikke fundet, noget gik galt!", Alert.WARNING);
            }

        } catch (SQLException sqlException) {
            throw new EntityException("Opgave blev ikke fundet, noget gik galt!", Alert.WARNING);
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
            throw new EntityException("Opgaver blev ikke fundet, noget gik galt!", Alert.WARNING);
        }
        return taskList;
    }

    @Override
    public List<Entity> getChildren(long taskId) {
        List<Entity> taskList = new ArrayList<>();
        String getTasks = """
                SELECT t.id,
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
                     LEFT JOIN status s
                               ON t.status = s.id
                WHERE t.parent_id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(getTasks);
            pstmt.setLong(1, taskId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = new Task(
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
                taskList.add(task);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Opgaver blev ikke fundet, noget gik galt!", Alert.WARNING);
        }

        return taskList;
    }

    @Override
    public boolean editEntity(Entity entity) {
        boolean isEdited;
        String edit = """
                UPDATE task SET name=?, description=?, deadline=?, estimated_hours=?, actual_hours=?, status=? WHERE id =?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(edit);
                pstmt.setString(1, entity.getName());
                pstmt.setString(2, entity.getDescription());
                pstmt.setDate(3, Date.valueOf(((Task) entity).getDeadline().toLocalDate()));
                pstmt.setInt(4, ((Task) entity).getEstimatedHours());
                pstmt.setInt(5, ((Task) entity).getActualHours());
                pstmt.setLong(6, ((Task) entity).getStatusId());
                pstmt.setLong(7, entity.getId());
                int affectedRows = pstmt.executeUpdate();

                isEdited = affectedRows > 0;

                connection.commit();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new EntityException("Opgave blev ikke opdateret, noget gik galt!", Alert.DANGER);
            }
        } catch (SQLException sqlException) {
            throw new EntityException("Opgave blev ikke opdateret, noget gik galt!", Alert.DANGER);
        }

        return isEdited;
    }

    @Override
    public boolean deleteEntity(long taskId) {
        boolean isDeleted;
        String deleteTask = """
                DELETE FROM task WHERE id = ?;
                """;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(deleteTask);
                pstmt.setLong(1, taskId);
                int affectedRows = pstmt.executeUpdate();

                isDeleted = affectedRows > 0;

            } catch (SQLException sqlException) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new EntityException("Opgave blev ikke slettet, noget gik galt!", Alert.DANGER);
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlException) {
            throw new EntityException("Opgave blev ikke slettet, noget gik galt!", Alert.DANGER);
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
    public List<Status> getStatusList() {
        List<Status> statusList = new ArrayList<>();
        String selectQuery = """
                SELECT name FROM status;
                """;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                statusList.add(Status.valueOf(rs.getString(1)));
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return statusList;
    }

    @Override
    public UserEntityRoleDto getUserFromParentId(String username, long parentId) {
        return null;
    }

    @Override
    public void assignMemberToEntity(long entityId, String username) {

    }

    @Override
    public void promoteMemberToAdmin(long entityId, String username) {

    }

    @Override
    public void kickMember(long entityId, String username) {

    }

    @Override
    public List<UserEntityRoleDto> getUsersFromParentIdAndEntityId(long parentId, long entityId) {
        return null;
    }
}
