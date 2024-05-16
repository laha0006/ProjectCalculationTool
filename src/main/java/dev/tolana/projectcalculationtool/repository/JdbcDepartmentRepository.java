package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.CreateDepartmentFormDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.util.RoleAssignUtil;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Department getDepartmentById(int deptId) {
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

    @Override
    public List<Department> getDepartmentsByOrganisationId(long id) {
        List<Department> departments = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from department where organisation_id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                departments.add(new Department(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return departments;
    }

    @Override
    public void createDepartment(CreateDepartmentFormDto departmentFormDto, String username) {

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setAutoCommit(false);

                String createDepartment = "INSERT INTO department(name, description, organisation_id) VALUES (?, ?, ?)";
                PreparedStatement pstmtAdd = connection.prepareStatement(createDepartment, Statement.RETURN_GENERATED_KEYS);
                pstmtAdd.setString(1, departmentFormDto.deptName());
                pstmtAdd.setString(2, departmentFormDto.deptDescription());
                pstmtAdd.setLong(3, departmentFormDto.organisationId());

                pstmtAdd.executeUpdate();
                ResultSet rs = pstmtAdd.getGeneratedKeys();
                if(rs.next()) {
                    long departmentId = rs.getLong(1);
                    RoleAssignUtil.assignDepartmentRole(connection, departmentId, UserRole.DEPARTMENT_OWNER, username);
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





    }


}
