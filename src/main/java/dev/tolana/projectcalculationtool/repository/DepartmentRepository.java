package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Department;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class DepartmentRepository {

    private DataSource dataSource;

    public DepartmentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Department getDepartment(int deptId) {
        Department department = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from department where id = ?");
            preparedStatement.setInt(1, deptId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                department = new Department(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return department;
    }
}
