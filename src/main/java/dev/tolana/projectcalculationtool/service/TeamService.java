package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.model.Team;
import dev.tolana.projectcalculationtool.repository.OrganisationRepository;
import dev.tolana.projectcalculationtool.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getTeamsByUser(String username) {
        return teamRepository.getTeamsByUser(username);
    }

    public List<Team> getNotArchivedTeamsByUser(String username) {
        List<Team> teams = teamRepository.getTeamsByUser(username);
        List<Team> notArchivedTeams = new ArrayList<>();
        for (Team team : teams) {
            if (!team.isArchived()) {
                notArchivedTeams.add(team);}
        }
        return notArchivedTeams;
    }

    public void createTeam(String username, String organisationName, String organisationDescription) {
    teamRepository.createTeam(username, organisationName, organisationDescription);
    }
}
