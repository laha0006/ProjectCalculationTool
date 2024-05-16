package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.model.Entity;
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
        List<EntityViewDto> listOfUserOrgs = organisationService.getNotArchivedOrganisationsByUser(username);
        model.addAttribute("allUserOrgs", listOfUserOrgs);

        return "organisation/userOrganisations";
    }

    @GetMapping("/{orgId}")
    public String organisationPage(@PathVariable("orgId") long organisationId, Model model) {
        Entity organisation = organisationService.getOrganisationsById(organisationId);
        List<EntityViewDto> departments = departmentService.getAll(organisationId);
        if (departments.isEmpty()) {
            model.addAttribute("alertWarning", "You're not part of any department.");
        }
        model.addAttribute("allDepartments", departments);
        model.addAttribute("organisation", organisation);
        return "organisation/organisationView";
    }

    @GetMapping("/create")
    public String createOrganisation(Model model) {
        EntityCreationDto emptyCreationDto = new EntityCreationDto("", "", 0);
        model.addAttribute("organisation", emptyCreationDto);

        return "organisation/createOrganisation";
    }

    @PostMapping("/create")
    public String createOrganisation(@ModelAttribute EntityCreationDto creationInfo, Authentication authentication) {
        String username = authentication.getName();
        organisationService.createOrganisation(username, creationInfo);
        return "redirect:/organisation";
    }

}
