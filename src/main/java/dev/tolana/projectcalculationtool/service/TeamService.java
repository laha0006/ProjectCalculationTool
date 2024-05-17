package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
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
    private final EntityDtoMapper entityDtoMapper;

    public TeamService(TeamRepository teamRepository, EntityDtoMapper entityDtoMapper) {
        this.teamRepository = teamRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

/* --old--
    public List<Team> getTeamsByUser(String username) {
        return teamRepository.getTeamsByUser(username);
    }
 */

    public EntityViewDto getTeam(long teamId){
        Entity team = teamRepository.getEntityOnId(teamId);
        return entityDtoMapper.convertToEntityViewDto(team);
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
