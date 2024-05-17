package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("organisation/{orgId}/department/{deptId}/team/{teamId}/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

//    @GetMapping("/overview")
//    public String getProjectOverview(Model model, Authentication authentication) {
//        String username = authentication.getName();
//        List<ProjectOverviewDto> projectList = projectService.getAllProjects(username);
//
//        model.addAttribute("projectList", projectList);
//        return "project/viewAllProjects";
//    }

    @GetMapping("/{projectId}")
    public String viewProject(Model model, @PathVariable long projectId) {
        ResourceEntityViewDto project = projectService.getProject(projectId);
        model.addAttribute("project", project);

        List<ResourceEntityViewDto> tasks = projectService.getChildren(projectId);
        model.addAttribute("allTasks", tasks);

        return "project/projectView";
    }

    @GetMapping("/create")
    public String showPageForAddingProject(Model model, @PathVariable long teamId) {
        model.addAttribute("newProject",new ProjectCreationDto("", "", teamId, LocalDateTime.now()));
        //TODO add something that makes it possible to display Team/Department/Organization/whatever

        return "project/createProject";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute Entity newProject, Authentication authentication) {
        String username = authentication.getName();
        projectService.createProject(username, newProject);

        return "redirect:/dashboard";
    }

    @GetMapping("/{projectId}/assign/members")
    public String getAllMembersFromTeamId(@PathVariable long teamId, @PathVariable long projectId, Model model, Authentication authentication) {
        ResourceEntityViewDto project = projectService.getProject(projectId);

        List<UserInformationDto> memberList = projectService.getAllTeamMembersFromTeamId(teamId);
        List<UserRole> userRoles = projectService.getAllUserRoles();

        model.addAttribute("projectName", project.resourceEntityName());
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

    @PostMapping("/{projectId}/delete")
    public String deleteProject(@PathVariable long orgId,
                                @PathVariable long deptId,
                                @PathVariable long teamId,
                                @PathVariable long projectId) {

        projectService.deleteProject(projectId);

        return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/overview";
    }
}
