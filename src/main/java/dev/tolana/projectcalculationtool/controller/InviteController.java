package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.InviteDto;
import dev.tolana.projectcalculationtool.dto.InviteFormDto;
import dev.tolana.projectcalculationtool.model.Invitation;
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
@RequestMapping("")
public class InviteController {

    private OrganisationService organisationService;
    private UserService userService;

    public InviteController(OrganisationService organisationService, UserService userService) {
        this.organisationService = organisationService;
        this.userService = userService;
    }

    @GetMapping("organisation/{orgId}/invite")
    public String invitePage(@PathVariable long orgId, Model model) {
        model.addAttribute("invite",new InviteFormDto("",orgId));
        List<Invitation> invitations = organisationService.getAllOutstandingInvitations(orgId);
        model.addAttribute("invitations", invitations);
        return "organisation/invite";
    }

    @PostMapping("organisation/invite")
    public String invite(InviteFormDto formdata, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        String referer = request.getHeader("referer");
        String userToBeInvited = formdata.username();
        userService.createInvite(formdata);
        redirectAttributes.addFlashAttribute("alertSuccess", "Du har inviteret " + userToBeInvited);
        return "redirect:" + referer;
    }

    @PostMapping("organisation/invite/remove")
    public String removeInvite(InviteFormDto inviteData, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        userService.removeInvite(inviteData);
        String referer = request.getHeader("referer");
        redirectAttributes.addFlashAttribute("alertWarning", "Du har taget invitationen tilbage");
        return "redirect:" + referer;
    }


    @GetMapping("user/invitations")
    public String invitations(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<InviteDto> invitations = userService.getInvitations(username);
        model.addAttribute("invitations", invitations);
        return "user/invitations";
    }

    @PostMapping("user/accept")
    public String acceptInvite(@RequestParam long orgId, RedirectAttributes redirectAttributes, Authentication authentication) {

        String username = authentication.getName();
        userService.acceptInvite(username,orgId);
        redirectAttributes.addFlashAttribute("alertSuccess", "Du har accepteret invitationen!");
        return "redirect:/user/invitations";
    }

    @PostMapping("user/decline")
    public String declineInvite(@RequestParam long orgId, RedirectAttributes redirectAttributes, Authentication authentication) {
        String username = authentication.getName();
        userService.declineInvite(username,orgId);
        redirectAttributes.addFlashAttribute("alertWarning", "Du har afvist invitationen!");
        return "redirect:/user/invitations";
    }



}
