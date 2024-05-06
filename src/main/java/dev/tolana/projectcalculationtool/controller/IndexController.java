package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.service.IndexService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    private final IndexService indexService;

    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }

}
