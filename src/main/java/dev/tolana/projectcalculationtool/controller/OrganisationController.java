package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.CreateOrganisationFormDto;
import dev.tolana.projectcalculationtool.model.Department;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.service.DepartmentService;
import dev.tolana.projectcalculationtool.service.OrganisationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/organisation")
public class OrganisationController {

    private final OrganisationService organisationService;
    private final DepartmentService departmentService;

    public OrganisationController(OrganisationService organisationService, DepartmentService departmentService) {
        this.organisationService = organisationService;
        this.departmentService = departmentService;
    }

    @GetMapping("")
    public String organisationMainPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Organisation> listOfUserOrgs = organisationService.getNotArchivedOrganisationsByUser(username);
        model.addAttribute("allUserOrgs", listOfUserOrgs);

        return "organisation/userOrganisations";
    }

    @GetMapping("/{orgId}")
    public String organisationPage(@PathVariable long orgId, Model model, Authentication authentication) {
        Organisation organisation = organisationService.getOrganisationsById(orgId);
        List<Department> departments = departmentService.getAll(orgId);
        if (departments.isEmpty()) {
            model.addAttribute("alertWarning", "You're not part of any department.");
        }
        model.addAttribute("allDepartments", departments);
        model.addAttribute("organisation", organisation);
        return "organisation/organisationView";
    }

    @GetMapping("/{orgId}/invite")
    public String invitePage(@PathVariable long orgId, Model model) {
        return "organisation/invite";
    }

    @GetMapping("/create")
    public String createOrganisation(Model model) {
        CreateOrganisationFormDto emptyOrganisationDto = new CreateOrganisationFormDto("", "");
        model.addAttribute("organisation", emptyOrganisationDto);

        return "organisation/createOrganisation";
    }

    @PostMapping("/create")
    public String createOrganisation(@ModelAttribute CreateOrganisationFormDto organisationDto, Authentication authentication) {
        String username = authentication.getName();
        String orgName = organisationDto.orgName();
        String orgDescription = organisationDto.orgDescription();
        organisationService.createOrganisation(username, orgName, orgDescription);
        return "redirect:/organisation";
    }

}
