package dev.tolana.projectcalculationtool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invitations")
public class InviteController {

    @GetMapping("")
    public String invitations() {
        return "invitations";
    }

    @PostMapping("/accept")
    public String accept() {
        return "accept";
    }
}
