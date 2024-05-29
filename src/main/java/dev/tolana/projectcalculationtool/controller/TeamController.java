package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.service.TeamService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/organisation/{orgId}/department/{deptId}/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/{teamId}")
    public String viewTeam(Model model, @PathVariable long orgId, @PathVariable long teamId) {
        EntityViewDto team = teamService.getTeam(teamId);
        model.addAttribute("team", team);

        List<ProjectViewDto> projects = teamService.getChildren(teamId);
        model.addAttribute("allProjects", projects);
        model.addAttribute("orgId", orgId);

        return "team/teamView";
    }

    @GetMapping("/create")
    public String createTeam(Model model, @PathVariable long orgId, @PathVariable long deptId) {
        EntityCreationDto emptyTeamDto = new EntityCreationDto("", "", deptId, EntityType.TEAM);
        model.addAttribute("team", emptyTeamDto);
        model.addAttribute("orgId", orgId);

        return "team/createTeam";
    }

    @PostMapping("/create")
    public String createTeam(@ModelAttribute EntityCreationDto teamDto,
                                     @PathVariable long orgId,
                                     @PathVariable long deptId,
                                     RedirectAttributes redirectAttributes,
                                     Authentication authentication) {

        teamService.createTeam(teamDto, authentication.getName());
        redirectAttributes.addFlashAttribute("alertSuccess", "Team created successfully");
        return "redirect:/" + "organisation/" + orgId + "/department/" + deptId;
    }

    @GetMapping("{teamId}/edit")
    public String editTeam(Model model, @PathVariable long teamId) {
        EntityEditDto team = teamService.getTeamToEdit(teamId);
        model.addAttribute("team", team);
        return "team/editTeam";
    }

    @PostMapping("{teamId}/edit")
    public String editTeamSave(@ModelAttribute EntityEditDto teamDto,@PathVariable long teamId) {
        teamService.editTeam(teamDto);
        return "redirect:/organisation/{orgId}/department/{deptId}/team/"+teamId;
    }

    @PostMapping("/{teamId}/delete")
    public String deleteProject(@PathVariable long orgId,
                                @PathVariable long deptId,
                                @PathVariable long teamId) {

        teamService.deleteTeam(teamId);

        return "redirect:/organisation/" + orgId + "/department/" + deptId;
    }


    @GetMapping("{teamId}/members")
    public String departmentMembersView(@PathVariable("orgId") long orgId,
                                        @PathVariable("deptId") long deptId,
                                        @PathVariable("teamId") long teamId, Model model){

        EntityViewDto team = teamService.getTeam(teamId);
        model.addAttribute("team", team);

        List<UserEntityRoleDto> users = teamService.getUsersFromDepartmentId(
                team.parentId(),teamId);
        model.addAttribute("teamUsers",users);

        model.addAttribute("organisationId",orgId);
        model.addAttribute("departmentId",deptId);

        return "team/viewMembers";
    }


    @PostMapping("/{teamId}/members/assign/{username}")
    public String assignMemberToTeam(@PathVariable("orgId") long orgId,
                                     @PathVariable("deptId") long deptId,
                                     @PathVariable("teamId") long teamId,
                                     @PathVariable("username") String username){



        teamService.assignMemberToTeam(teamId,username);


        return "redirect:/organisation/" + orgId + "/department/"+ deptId + "/team/" + teamId +"/members";
    }

    @PostMapping("/{teamId}/members/promote/{username}")
    public String promoteMemberToAdmin(@PathVariable("orgId") long orgId,
                                       @PathVariable("deptId") long deptId,
                                       @PathVariable("teamId") long teamId,
                                       @PathVariable("username") String username){


        teamService.promoteMemberToAdmin(teamId,username);


        return "redirect:/organisation/" + orgId + "/department/"+ deptId + "/team/" + teamId +"/members";
    }

    @PostMapping("/{teamId}/members/kick/{username}")
    public String kickMemberFromDepartment(@PathVariable("orgId") long orgId,
                                           @PathVariable("deptId") long deptId,
                                           @PathVariable("teamId") long teamId,
                                           @PathVariable("username") String username){


        teamService.kickMemberFromTeam(teamId,username);


        return "redirect:/organisation/" + orgId + "/department/"+ deptId + "/team/" + teamId +"/members";
    }
}