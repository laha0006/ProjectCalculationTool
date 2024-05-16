package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.ProjectOverviewDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("organisation/{orgId}/department/{deptId}/team/{teamId}/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/addproject")
    public String showPageForAddingProject(Model model) {
        Project newProject = new Project(); //TODO MAKE DTO INSTEAD OF MODEL
        model.addAttribute("newProject",newProject);
        //TODO add something that makes it possible to display Team/Department/Organization/whatever

        return "project/create";
    }

    @PostMapping("/addproject")
    public String addProject(@ModelAttribute Entity newProject, Authentication authentication) {
        String username = authentication.getName();
        projectService.addProject(username, newProject);

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
    public String getAllMembersFromTeamId(@PathVariable long teamId, @PathVariable long projectId, Model model, Authentication authentication) {
        ProjectOverviewDto project = projectService.getProjectOnId(projectId);

        List<UserInformationDto> memberList = projectService.getAllTeamMembersFromTeamId(teamId);
        Set<UserRole> userRoles = projectService.getAllUserRoles();

        model.addAttribute("projectName", project.name());
        model.addAttribute("teamMembers", memberList);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("roleMember", UserRole.PROJECT_MEMBER);
        model.addAttribute("projectId", projectId);

        return "project/viewAllTeamMembers";
    }

    @PostMapping("/{projectId}/assign/members")
    public String assignTeamMembersToProject(@RequestParam(value="teamMember", required = false) List<String> selectedTeamMembers,
                                             @RequestParam UserRole role,
                                             @PathVariable long projectId) {
        if (!selectedTeamMembers.isEmpty()) {
            projectService.assignTeamMembersToProject(projectId, selectedTeamMembers, role);
        }

        return "redirect:/project/overview";
    }
}
