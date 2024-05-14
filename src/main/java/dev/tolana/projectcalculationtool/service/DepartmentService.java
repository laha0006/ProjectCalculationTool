package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department getDepartment(int deptId) {
        return departmentRepository.getDepartment(deptId);
    }
}
