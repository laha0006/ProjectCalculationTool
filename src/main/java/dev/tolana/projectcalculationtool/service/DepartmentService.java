package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.CreateDepartmentFormDto;
import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.repository.JdbcDepartmentRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private JdbcDepartmentRepository departmentRepository;

    public DepartmentService(JdbcDepartmentRepository jdbcDepartmentRepository) {
        this.departmentRepository = jdbcDepartmentRepository;
    }

    public Department getDepartment(int deptId) {
        return departmentRepository.getDepartmentById(deptId);
    }

    public void createDepartment(CreateDepartmentFormDto departmentFormDto, String username) {
        departmentRepository.createDepartment(departmentFormDto, username);
    }
//    @PreAuthorize("@auth.hasAccess(#id, T(dev.tolana.projectcalculationtool.enums.Permission).ORGANISATION_READ)")

    //filterObject referes to the current object of the collection, when looping.
    @PostFilter("@auth.hasDepartmentAccess(filterObject.id, T(dev.tolana.projectcalculationtool.enums.Permission).DEPARTMENT_READ)")
    public List<Department> getAll(long organsationId) {
        return departmentRepository.getDepartmentsByOrganisationId(organsationId);
    }


}
