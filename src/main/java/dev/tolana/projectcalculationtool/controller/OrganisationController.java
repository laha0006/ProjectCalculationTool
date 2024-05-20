package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.EntityType;
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

    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
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
        EntityViewDto organisation = organisationService.getOrganisation(organisationId);
        model.addAttribute("organisation", organisation);

        List<EntityViewDto> departments = organisationService.getChildren(organisationId);
        if (departments.isEmpty()) {
            model.addAttribute("alertWarning", "Du er ikke medlem af nogle afdelinger!");
        }
        model.addAttribute("allDepartments", departments);
        return "organisation/organisationView";
    }

    @GetMapping("/{orgId}/members")
    public String organisationMembersView(@PathVariable("orgId") long organisationId, Model model){
        EntityViewDto organisation = organisationService.getOrganisation(organisationId);
        model.addAttribute("organisation", organisation);

        List<UserEntityRoleDto> users = organisationService.getUsersFromOrganisationId(organisationId);
        model.addAttribute("users",users);

        return "organisation/viewMembers";
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
