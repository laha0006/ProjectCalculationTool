package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.service.CalculationService;
import dev.tolana.projectcalculationtool.service.OrganisationService;
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

    @GetMapping("/{projectId}")
    public String viewProject(Model model, @PathVariable long orgId, @PathVariable long deptId, @PathVariable long teamId, @PathVariable long projectId) {
        ResourceEntityViewDto project = projectService.getProject(projectId);
        model.addAttribute("project", project);

        List<ResourceEntityViewDto> tasks = projectService.getTasks(projectId);
        model.addAttribute("allTasks", tasks);

        List<ResourceEntityViewDto> allSubProjects = projectService.getSubProjects(projectId);
        model.addAttribute("allSubProjects", allSubProjects);



        ProjectStatsDto stats = projectService.getProjectStats(projectId);
        model.addAttribute("projectStats", stats);

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);

        return "project/projectView";
    }

    @GetMapping("/create")
    public String showPageForCreatingProject(Model model,
                                           @PathVariable long orgId,
                                           @PathVariable long deptId,
                                           @PathVariable long teamId) {
        model.addAttribute("newProject",new ProjectCreationDto("", "", -1, teamId, LocalDateTime.now()));
        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        //TODO add something that makes it possible to display Team/Department/Organization/whatever

        return "project/createProject";
    }

    @GetMapping("/{projectId}/create/subproject")
    public String showSubProjectCreationPage(Model model,
                                             @PathVariable long orgId,
                                             @PathVariable long deptId,
                                             @PathVariable long teamId,
                                             @PathVariable long projectId) {
        model.addAttribute("newProject",new ProjectCreationDto("", "", projectId, teamId, LocalDateTime.now()));
        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);

        return "project/createProject";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute ProjectCreationDto newProject,
                                @PathVariable long orgId,
                                @PathVariable long deptId,
                                @PathVariable long teamId,
                                Authentication authentication) {

        String username = authentication.getName();
        projectService.createProject(username, newProject);

        return "redirect:/" + "organisation/" + orgId + "/department/" + deptId + "/team/" + teamId;
    }

    @GetMapping("/{projectId}/assign/members")
    public String getAllMembersFromTeamId(@PathVariable long teamId, @PathVariable long projectId, Model model, @PathVariable String deptId, @PathVariable String orgId) {
        ResourceEntityViewDto project = projectService.getProject(projectId);

        List<UserInformationDto> memberList = projectService.getAllTeamMembersFromTeamId(teamId);
        List<UserRole> userRoles = projectService.getAllUserRoles();

        model.addAttribute("projectName", project.resourceEntityName());
        model.addAttribute("teamMembers", memberList);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("roleMember", UserRole.PROJECT_MEMBER);
        model.addAttribute("projectId", projectId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("orgId", orgId);

        return "project/viewAllTeamMembers";
    }

    @PostMapping("/{projectId}/assign/members")
    public String assignTeamMembersToProject(@RequestParam(value="teamMember", required = false) List<String> selectedTeamMembers,
                                             @RequestParam UserRole role,
                                             @PathVariable long orgId,
                                             @PathVariable long deptId,
                                             @PathVariable long teamId,
                                             @PathVariable long projectId) {
        if (!selectedTeamMembers.isEmpty()) {
            projectService.assignTeamMembersToProject(projectId, selectedTeamMembers, role);
        }

        return "redirect:/organisation/" + orgId + "/department/" +  deptId +"/team/" + teamId;
    }

    @PostMapping("/{projectId}/delete")
    public String deleteProject(@PathVariable long orgId,
                                @PathVariable long deptId,
                                @PathVariable long teamId,
                                @PathVariable long projectId) {

        projectService.deleteProject(projectId);
        return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId;
    }
}
