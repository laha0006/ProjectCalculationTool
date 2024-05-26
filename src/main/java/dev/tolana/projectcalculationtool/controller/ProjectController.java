package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
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
        ProjectViewDto project = projectService.getProjectToView(projectId);
        model.addAttribute("project", project);

        List<TaskViewDto> tasks = projectService.getTasks(projectId);
        model.addAttribute("allTasks", tasks);

        List<ProjectViewDto> allSubProjects = projectService.getSubProjects(projectId);
        model.addAttribute("allSubProjects", allSubProjects);

        ProjectStatsDto stats = projectService.getProjectStats(projectId);
        model.addAttribute("projectStats", stats);

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("projectId", projectId);

        return "project/projectView";
    }

    @GetMapping("/create")
    public String showPageForCreatingProject(Model model,
                                             @PathVariable long orgId,
                                             @PathVariable long deptId,
                                             @PathVariable long teamId) {
        ProjectCreationDto projectToCreate = new ProjectCreationDto("", "", 0, teamId, LocalDateTime.now(), 0);

        model.addAttribute("projectToCreate", projectToCreate);
        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);

        return "project/createProject";
    }

    @GetMapping("/{projectId}/create/subproject")
    public String showSubProjectCreationPage(Model model,
                                             @PathVariable long orgId,
                                             @PathVariable long deptId,
                                             @PathVariable long teamId,
                                             @PathVariable long projectId) {

        ProjectCreationDto subProjectToCreate = new ProjectCreationDto("", "", projectId, teamId, LocalDateTime.now(), 0);
        model.addAttribute("subProjectToCreate", subProjectToCreate);
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
        long projectParentId = newProject.parentId();

        return determineRedirection(orgId, deptId, teamId, projectParentId);
    }

    @GetMapping("/{projectId}/assign/members")
    public String getAllMembersFromTeamId(@PathVariable long teamId, @PathVariable long projectId, Model model, @PathVariable String deptId, @PathVariable String orgId) {
        ProjectViewDto project = projectService.getProjectToView(projectId);
        model.addAttribute("projectName", project.projectName());

        List<UserInformationDto> memberList = projectService.getAllTeamMembersFromTeamId(teamId);
        model.addAttribute("teamMembers", memberList);

        List<UserRole> userRoles = projectService.getAllUserRoles();
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("roleMember", UserRole.PROJECT_MEMBER);

        model.addAttribute("projectId", projectId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("orgId", orgId);

        return "project/viewAllTeamMembers";
    }

    //obsolete with US36
    @PostMapping("/{projectId}/assign/members")
    public String assignTeamMembersToProject(@RequestParam(value = "teamMember", required = false) List<String> selectedTeamMembers,
                                             @RequestParam UserRole role,
                                             @PathVariable long orgId,
                                             @PathVariable long deptId,
                                             @PathVariable long teamId,
                                             @PathVariable long projectId) {

        if (!selectedTeamMembers.isEmpty()) {
            projectService.assignTeamMembersToProject(projectId, selectedTeamMembers, role);
        }

        return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId;
    }

    @PostMapping("/{projectId}/delete")
    public String deleteProject(@PathVariable long orgId,
                                @PathVariable long deptId,
                                @PathVariable long teamId,
                                @PathVariable long projectId) {

        ProjectViewDto projectToDelete = projectService.getProjectToView(projectId);
        long projectParentId = projectToDelete.parentId();

        projectService.deleteProject(projectId);

        return determineRedirection(orgId, deptId, teamId, projectParentId);
    }

    @GetMapping("{projectId}/edit")
    public String displayEditProjectPage(@PathVariable long orgId,
                                         @PathVariable long deptId,
                                         @PathVariable long teamId,
                                         @PathVariable long projectId,
                                         Model model) {

        List<Status> statusList = projectService.getStatusList();
        model.addAttribute("statusList", statusList);

        ProjectEditDto projectToEdit = projectService.getProjectToEdit(projectId);
        model.addAttribute("projectToEdit", projectToEdit);

        model.addAttribute("projectId", projectId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("orgId", orgId);

        return "project/editProject";
    }

    @PostMapping("{projectId}/edit")
    public String editProject(@PathVariable long orgId,
                              @PathVariable long deptId,
                              @PathVariable long teamId,
                              @ModelAttribute ProjectEditDto projectToEdit) {

        long projectId = projectToEdit.id();
        projectService.editProject(projectToEdit);

        return "redirect:/" + "organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId;
    }

    private String determineRedirection(long orgId, long deptId, long teamId, long projectParentId) {
        if (projectParentId == 0) {
            return "redirect:/" + "organisation/" + orgId + "/department/" + deptId + "/team/" + teamId;

        } else {
            return "redirect:/" + "organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectParentId;
        }
    }


    @GetMapping("{projectId}/members")
    public String projectMembersView(@PathVariable("orgId") long orgId,
                                     @PathVariable("deptId") long deptId,
                                     @PathVariable("teamId") long teamId,
                                     @PathVariable("teamId") long projectId, Model model){
        //TODO exclude owner of department from results
        ProjectViewDto project = projectService.getProjectToView(projectId);
        model.addAttribute("project", project);

        List<UserEntityRoleDto> users = projectService.getUsersFromTeamId(
                project.parentId(),projectId);
        model.addAttribute("projectUsers",users);

        //used for redirect by storing the values from the url and using them on the return button
        model.addAttribute("organisationId",orgId);
        model.addAttribute("departmentId",deptId);
        model.addAttribute("teamId",teamId);

        return "project/viewMembers";
    }


    @PostMapping("/{projectId}/members/assign/{username}")
    public String assignMemberToProject(@PathVariable("orgId") long orgId,
                                     @PathVariable("deptId") long deptId,
                                     @PathVariable("teamId") long teamId,
                                     @PathVariable("projectId") long projectId,
                                     @PathVariable("username") String username){

        UserEntityRoleDto user = projectService.getUserFromTeamId(username,
                teamId);

        projectService.assignMemberToProject(projectId,user.username());


        return "redirect:/organisation/" + orgId + "/department/"+ deptId +
                "/team/" + teamId + "/project/" + projectId + "/members";
    }

    @PostMapping("/{projectId}/members/promote/{username}")
    public String promoteMemberToAdmin(@PathVariable("orgId") long orgId,
                                       @PathVariable("deptId") long deptId,
                                       @PathVariable("teamId") long teamId,
                                       @PathVariable("projectId") long projectId,
                                       @PathVariable("username") String username){

        UserEntityRoleDto user = projectService.getUserFromTeamId(username,
                teamId);

        projectService.promoteMemberToAdmin(projectId,user.username());


        return "redirect:/organisation/" + orgId + "/department/"+ deptId +
                "/team/" + teamId + "/project/" + projectId + "/members";
    }

    @PostMapping("/{projectId}/members/kick/{username}")
    public String kickMemberFromDepartment(@PathVariable("orgId") long orgId,
                                           @PathVariable("deptId") long deptId,
                                           @PathVariable("teamId") long teamId,
                                           @PathVariable("projectId") long projectId,
                                           @PathVariable("username") String username){

        UserEntityRoleDto user = projectService.getUserFromTeamId(username,
                teamId);

        projectService.kickMemberFromProject(projectId,user.username());

        return "redirect:/organisation/" + orgId + "/department/"+ deptId +
                "/team/" + teamId + "/project/" + projectId + "/members";
    }
}
