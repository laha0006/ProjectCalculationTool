package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.UserDto;
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
        model.addAttribute("newUser", new UserDto("username", "password"));

        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserDto newUser) {
        userService.registerUser(newUser);

        return "redirect:/user/login";
    }
}
