package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.repository.DepartmentRepository;
import dev.tolana.projectcalculationtool.repository.EntityCrudOperations;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcDepartmentRepository implements DepartmentRepository {

    private DataSource dataSource;

    public JdbcDepartmentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createEntity(String username, Entity entity) {
        boolean isCreated = false;

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createDepartment = "INSERT INTO department(name, description, organisation_id) VALUES (?, ?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createDepartment,
                        Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, entity.getName());
                pstmtAdd.setString(2, entity.getDescription());
                pstmtAdd.setLong(3, ((Department)entity).getOrganisationId());
                int affectedRows = pstmtAdd.executeUpdate();
                isCreated = affectedRows > 0;

                ResultSet rs = pstmtAdd.getGeneratedKeys();
                if(rs.next()) {
                    long departmentId = rs.getLong(1);
                    RoleAssignUtil.assignDepartmentRole(connection, departmentId,
                            UserRole.DEPARTMENT_OWNER, username);
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
    public List<Entity> getAllEntitiesOnUsername(String username) {
        return null;
    }

    @Override
    public List<Entity> getAllEntitiesOnId(long organisationId) {
        List<Entity> departments = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from department where organisation_id = ?");
            preparedStatement.setLong(1, organisationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                departments.add(new Department(
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
        return departments;
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
