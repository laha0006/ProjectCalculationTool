package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.CreateOrganisationFormDto;
import dev.tolana.projectcalculationtool.dto.CreateTeamFormDto;
import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.model.Team;
import dev.tolana.projectcalculationtool.service.OrganisationService;
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
    public String team(Model model, @PathVariable("teamId") long teamId) {
        EntityViewDto team = teamService.getTeam(teamId);
        model.addAttribute("team", team);

        return "team/teamView";
    }

    @GetMapping("/create")
    public String createTeam(Model model, @PathVariable long deptId) {
        EntityCreationDto emptyTeamDto = new EntityCreationDto("", "", deptId);
        model.addAttribute("team", emptyTeamDto);

        return "team/createTeam";
    }

    @PostMapping("/create")
    public String createOrganisation(@ModelAttribute EntityCreationDto teamDto,
                                     RedirectAttributes redirectAttributes,
                                     Authentication authentication) {

        teamService.createTeam(teamDto, authentication.getName());
        redirectAttributes.addFlashAttribute("alertSuccess", "Team created successfully");
        return "redirect:/department/" + teamDto.foreignEntityId();
    }

}
