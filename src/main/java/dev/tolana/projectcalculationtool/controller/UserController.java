package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.RegisterUserDto;
import dev.tolana.projectcalculationtool.service.UserService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String invitations(Model model) {
        return "user/invitations";
    }

    @PostMapping("/accept")
    public String acceptInvite() {
        int orgid = 1;
        return "redirect:/organisation/"+ orgid;
    }
}
