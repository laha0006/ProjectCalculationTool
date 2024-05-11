package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.model.Project;

import java.util.List;

public interface ProjectRepository {

    int addProject(Project project);

    List<Project> getAllProjectsOnUsername(String username);

    long getTeamIdFromUsername(String username);

    List<UserInformationDto> getTeamMembersFromTeamId(long teamId, long projectId);

    void assignTeamMembersToProject(long projectId, List<String> selectedTeamMembers);

    Project getProjectOnId(long projectId);
}
