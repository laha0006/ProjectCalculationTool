package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.service.DepartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organisation/{org_id}/department")
public class DepartmentController {

    private DepartmentService departmentService;

    @GetMapping("/{dept_id}")
    public String department(@PathVariable int dept_id, Model model) {
        Department department = departmentService.getDepartment(dept_id);
        model.addAttribute("department", department);

        return "department/departmentView";
    }
}
