package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.dto.InviteDto;
import dev.tolana.projectcalculationtool.dto.InviteFormDto;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Invitation;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.service.DepartmentService;
import dev.tolana.projectcalculationtool.service.OrganisationService;
import dev.tolana.projectcalculationtool.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        EntityViewDto organisation = organisationService.getOrganisationsById(organisationId);
        List<EntityViewDto> departments = departmentService.getAll(organisationId);
        if (departments.isEmpty()) {
            model.addAttribute("alertWarning", "Du er ikke medlem af nogle afdelinger!");
        }
        model.addAttribute("allDepartments", departments);
        model.addAttribute("organisation", organisation);
        return "organisation/organisationView";
    }

    @GetMapping("/create")
    public String createOrganisation(Model model) {
        EntityCreationDto emptyCreationDto = new EntityCreationDto("", "", -1, EntityType.ORGANISATION);
        model.addAttribute("organisation", emptyCreationDto);

        return "organisation/createOrganisation";
    }

    @PostMapping("/create")
    public String createOrganisation(@ModelAttribute EntityCreationDto creationInfo, Authentication authentication) {
        String username = authentication.getName();
        organisationService.createOrganisation(username, creationInfo);
        return "redirect:/organisation";
    }

    @PostMapping("/{organisationId}/delete")
    public String deleteOrganisation(@PathVariable long organisationId) {
        organisationService.deleteOrganisation(organisationId);

        return "redirect:/organisation";
    }

}
