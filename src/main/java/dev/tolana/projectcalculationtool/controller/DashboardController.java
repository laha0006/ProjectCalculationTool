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
}
