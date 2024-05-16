package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.ProjectOverviewDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import dev.tolana.projectcalculationtool.repository.ResourceEntityCrudOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EntityDtoMapper entityDtoMapper;

    public ProjectService(ProjectRepository projectRepository, EntityDtoMapper entityDtoMapper) {
        this.projectRepository = projectRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

    public void addProject(String username, Entity project) {
        projectRepository.createEntity(username, project);
    }

    public List<ProjectOverviewDto> getAllProjects(String username) {
        List<Entity> projectList = projectRepository.getAllEntitiesOnUsername(username);
        return entityDtoMapper.toProjectOverviewDtoList(projectList);
    }

    public List<UserInformationDto> getAllTeamMembersFromTeamId(long teamId) {
        return projectRepository.getUsersFromEntityId(teamId);
    }

    public void assignTeamMembersToProject(long projectId, List<String> selectedTeamMembers, UserRole userRole) {
        projectRepository.assignUser(projectId, selectedTeamMembers, userRole);
    }

    public ProjectOverviewDto getProjectOnId(long projectId) {
        Entity project = projectRepository.getEntityOnId(projectId);
        return entityDtoMapper.toProjectOverviewDto((Project) project);
    }

    public List<UserRole> getAllUserRoles() {
        return projectRepository.getAllUserRoles();
    }
}
