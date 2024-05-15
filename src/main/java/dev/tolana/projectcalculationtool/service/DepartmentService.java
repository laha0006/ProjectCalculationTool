package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.repository.DepartmentRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {

    private DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department getDepartment(int deptId) {
        return departmentRepository.getDepartment(deptId);
    }


//    @PreAuthorize("@auth.hasAccess(#id, T(dev.tolana.projectcalculationtool.enums.Permission).ORGANISATION_READ)")

    //filterObject referes to the current object of the collection, when looping.
    @PostFilter("@auth.hasDepartmentAccess(filterObject.id, T(dev.tolana.projectcalculationtool.enums.Permission).DEPARTMENT_READ)")
    public List<Department> getAll(long organsationId) {
        return departmentRepository.getAll(organsationId);
    }
}
