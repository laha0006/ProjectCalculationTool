package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("")
    public String displayDashboardOnUser(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<UserEntityRoleDto> userEntityRoleList = indexService.getUserEntityRoleListOnUsername(username);
        model.addAttribute("user", userEntityRoleList);

        return "index";
    }
}
