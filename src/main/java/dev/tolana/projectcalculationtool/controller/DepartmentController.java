package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.service.DepartmentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organisation/{orgId}/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/{deptId}")
    public String department(@PathVariable("deptId") long departmentId , Model model) {
        EntityViewDto department = departmentService.getDepartment(departmentId);
        model.addAttribute("department", department);

        return "department/departmentView";
    }

    @GetMapping("/create")
    public String createDepartment(Model model, @PathVariable long orgId) {
        EntityCreationDto emptyDepartmentDto = new EntityCreationDto("", "", orgId, EntityType.DEPARTMENT);
        model.addAttribute("department", emptyDepartmentDto);
        return "department/createDepartment";
    }

    @PostMapping("/create")
    public String createDepartment(@ModelAttribute EntityCreationDto departmentDto, RedirectAttributes redirectAttributes, Authentication authentication) {
        departmentService.createDepartment(departmentDto, authentication.getName());
        redirectAttributes.addFlashAttribute("alertSuccess", "Department created successfully");
        return "redirect:/organisation/" + departmentDto.parentId();
    }
}
