package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.CreateOrganisationFormDto;
import dev.tolana.projectcalculationtool.dto.CreateTeamFormDto;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.model.Team;
import dev.tolana.projectcalculationtool.service.OrganisationService;
import dev.tolana.projectcalculationtool.service.TeamService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("")
    public String teamMainPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Team> listOfUserTeams = teamService.getNotArchivedTeamsByUser(username);
        model.addAttribute("allUserTeams", listOfUserTeams);

        return "team/userTeams";
    }

    @GetMapping("/create")
    public String createTeam(Model model) {
        CreateTeamFormDto emptyTeamDto = new CreateTeamFormDto("", "");
        model.addAttribute("team", emptyTeamDto);

        return "team/createTeam";
    }

    @PostMapping("/create")
    public String createOrganisation(@ModelAttribute CreateTeamFormDto teamFormDto, Authentication authentication) {
        String username = authentication.getName();
        String orgName = teamFormDto.teamName();
        String orgDescription = teamFormDto.teamDescription();
        teamService.createTeam(username, orgName, orgDescription);
        return "redirect:/team";
    }

}
