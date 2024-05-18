package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.dto.ResourceEntityViewDto;
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

        List<ResourceEntityViewDto> projects = teamService.getAllChildren(teamId);
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
}