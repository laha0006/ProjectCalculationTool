package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.RegisterUserDto;
import dev.tolana.projectcalculationtool.dto.inviteDto;
import dev.tolana.projectcalculationtool.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("newUser", new RegisterUserDto("username", "password"));

        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterUserDto newUser) {
        userService.registerUser(newUser);

        return "redirect:/user/login";
    }

    @GetMapping("/invitations")
    public String invitations(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<inviteDto> invitations = userService.getInvitations(username);
        model.addAttribute("invitations", invitations);
        return "user/invitations";
    }

    @PostMapping("/accept")
    public String acceptInvite(@RequestParam long orgId, RedirectAttributes redirectAttributes, Authentication authentication) {
        System.out.println("WE ACCEPTED???");
        String username = authentication.getName();
        userService.acceptInvite(username,orgId);
        redirectAttributes.addFlashAttribute("alertSuccess", "Du har accepteret invitationen!");
        return "redirect:/user/invitations";
    }

    @PostMapping("/decline")
    public String declineInvite(@RequestParam long orgId) {
        return "redirect:/organisation/"+ orgId;
    }

    @GetMapping("/inviteCount")
    public int getInviteCount(Authentication authentication) {
        String username = authentication.getName();
        return userService.getInvitationsCount(username);
    }
}
