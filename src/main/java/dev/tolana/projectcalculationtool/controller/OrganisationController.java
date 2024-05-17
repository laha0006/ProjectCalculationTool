package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.dto.InviteDto;
import dev.tolana.projectcalculationtool.dto.InviteFormDto;
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
    private final UserService userService;

    public OrganisationController(OrganisationService organisationService, DepartmentService departmentService, UserService userService) {
        this.organisationService = organisationService;
        this.departmentService = departmentService;
        this.userService = userService;
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

    @GetMapping("/{orgId}/invite")
    public String invitePage(@PathVariable long orgId, Model model) {
        model.addAttribute("invite",new InviteFormDto("",orgId));
        List<Invitation> invitations = organisationService.getAllOutstandingInvitations(orgId);
        model.addAttribute("invitations", invitations);
        return "organisation/invite";
    }

    @PostMapping("/invite")
    public String invite(InviteFormDto formdata, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        System.out.println("##################### POSTED LOL ");
        String referer = request.getHeader("referer");
        String userToBeInvited = formdata.username();
        userService.createInvite(formdata);
        redirectAttributes.addFlashAttribute("alertSuccess", "Du har inviteret " + userToBeInvited);
        return "redirect:" + referer;
    }

    @PostMapping("/invite/remove")
    public String removeInvite(InviteFormDto inviteData, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        userService.removeInvite(inviteData);
        String referer = request.getHeader("referer");
        redirectAttributes.addFlashAttribute("alertWarning", "Du har taget invitationen tilbage");
        return "redirect:" + referer;
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
