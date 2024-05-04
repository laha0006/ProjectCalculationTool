package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.service.IndexService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class IndexController {

    private final IndexService indexService;

    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("")
    public String displayDashboardOnUser(Model model, Authentication authentication) {
        String username = authentication.getName();
        UserEntityRoleDto user = indexService.getUserEntityRoleOnUsername(username);
        model.addAttribute("user", user);

        return "index";
    }
}
