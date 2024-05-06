package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.model.Organization;
import dev.tolana.projectcalculationtool.service.OrganizationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("")
    public String organizationMainPage (Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Organization> listOfUserOrgs = organizationService.getNotArchivedOrganizationsByUser(username);
        model.addAttribute("allUserOrgs",listOfUserOrgs);

        return "organization/userOrganizations";
    }

    @GetMapping("/create")
    public String createOrganization(Model model) {

    }

}
