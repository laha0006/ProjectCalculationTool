package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("about")
    public String about() {
        return "about";
    }
}
