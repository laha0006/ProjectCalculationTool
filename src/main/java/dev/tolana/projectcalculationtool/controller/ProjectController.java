package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/addproject")
    public String showPageForAddingProject(Model model) {
        Project newProject = new Project();
        model.addAttribute("newProject",newProject);
        //TODO add something that makes it possible to display Team/Department/Organization/whatever

        return "project/create";
    }

    @PostMapping("/addproject")
    public String addProject(@ModelAttribute Project newProject) {
        projectService.addProject(newProject);

        return "user/dashboard";
    }

    @GetMapping("/overview")
    public String getProjectOverview(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Project> projectList = projectService.getAllProjects(username);

        model.addAttribute("projectList", projectList);
        return "project/viewAllProjects";
    }
}
