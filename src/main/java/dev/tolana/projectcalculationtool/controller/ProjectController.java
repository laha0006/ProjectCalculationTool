package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.ProjectOverviewDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        return "redirect:/dashboard";
    }

    @GetMapping("/overview")
    public String getProjectOverview(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<ProjectOverviewDto> projectList = projectService.getAllProjects(username);

        model.addAttribute("projectList", projectList);
        return "project/viewAllProjects";
    }

    @GetMapping("/{projectId}/assign/members")
    public String getAllMembersFromTeamId(@PathVariable long projectId, Model model, Authentication authentication) {
        String username = authentication.getName();
        long teamId = projectService.getTeamIdFromUsername(username);
        ProjectOverviewDto project = projectService.getProjectOnId(projectId);

        List<UserInformationDto> memberList = projectService.getAllTeamMembersFromTeamId(teamId, projectId);
        model.addAttribute("projectName", project.name());
        model.addAttribute("teamMembers", memberList);
        model.addAttribute("projectId", projectId);

        return "project/viewAllTeamMembers";
    }

    @PostMapping("/{projectId}/assign/members")
    public String assignTeamMembersToProject(@RequestParam(value="teamMember", required = false) List<String> selectedTeamMembers,
                                             @PathVariable long projectId) {
        if (!selectedTeamMembers.isEmpty()) {
            projectService.assignTeamMembersToProject(projectId, selectedTeamMembers);
        }

        return "redirect:/project/overview";
    }
}
