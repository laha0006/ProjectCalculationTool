package dev.tolana.projectcalculationtool.repository.impl;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.repository.DepartmentRepository;
import dev.tolana.projectcalculationtool.repository.EntityCrudOperations;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        return false;
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
    public List<Entity> getAllEntitiesOnId(long departmentId) {
        List<Entity> departments = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from department where organisation_id = ?");
            preparedStatement.setLong(1, departmentId);
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
