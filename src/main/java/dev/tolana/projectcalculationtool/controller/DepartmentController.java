package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.CreateDepartmentFormDto;
import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.service.DepartmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organisation/{orgId}/department")
public class DepartmentController {

    private DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;}

    @GetMapping("/{deptId}")
    public String department(@PathVariable int deptId, Model model) {
        Department department = departmentService.getDepartment(deptId);
        model.addAttribute("department", department);

        return "department/departmentView";
    }

    @GetMapping("/create")
    public String createDepartment(Model model, @PathVariable long orgId) {
        CreateDepartmentFormDto emptyDepartmentDto = new CreateDepartmentFormDto("", "", orgId);
        model.addAttribute("department", emptyDepartmentDto);
        return "department/createDepartment";
    }

    @PostMapping("/create")
    public String createDepartment(@ModelAttribute CreateDepartmentFormDto departmentDto, RedirectAttributes redirectAttributes, Authentication authentication) {
        departmentService.createDepartment(departmentDto, authentication.getName());
        redirectAttributes.addFlashAttribute("alertSuccess", "Department created successfully");
        return "redirect:/organisation/" + departmentDto.organisationId();
    }
}
