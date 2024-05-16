package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.CreateDepartmentFormDto;
import dev.tolana.projectcalculationtool.model.Department;

import java.util.List;

public interface DepartmentRepository {

    Department getDepartmentById(int deptId);

    List<Department> getDepartmentsByOrganisationId(long id);

    void createDepartment(CreateDepartmentFormDto departmentFormDto, String username);


}
