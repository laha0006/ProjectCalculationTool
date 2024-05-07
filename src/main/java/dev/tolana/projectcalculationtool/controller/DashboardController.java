package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("")
    public String displayDashboardOnUser(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<UserEntityRoleDto> userEntityRoleList = dashboardService.getUserEntityRoleListOnUsername(username);
        model.addAttribute("username", username);

        return "user/dashboard";
    }

    @GetMapping("/addproject")
    public String showPageForAddingProject(Model model) {
        Project newProject = new Project();
        model.addAttribute("newProject",newProject);
        //TODO add something that makes it possible to display Team/Department/Organization/whatever

        return "project/create";
    }

    @PostMapping("/addproject")
    public String addProject(@ModelAttribute Project newProject) {
        dashboardService.addProject(newProject);

        return "user/dashboard";
    }
}
