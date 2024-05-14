package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.model.Team;

import java.util.List;

public interface TeamRepository {
    List<Team> getTeamsByUser(String username);

    void createTeam(String username, String teamName, String teamDescription);
}
