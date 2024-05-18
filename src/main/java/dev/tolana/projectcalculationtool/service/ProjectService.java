package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.ProjectOverviewDto;
import dev.tolana.projectcalculationtool.dto.ResourceEntityViewDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.model.ResourceEntity;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EntityDtoMapper entityDtoMapper;

    public ProjectService(ProjectRepository projectRepository, EntityDtoMapper entityDtoMapper) {
        this.projectRepository = projectRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

    public ResourceEntityViewDto getProject(long projectId) {
        Entity project = projectRepository.getEntityOnId(projectId);
        return entityDtoMapper.toResourceEntityViewDto(project);
    }

    public void createProject(String username, Entity project) {
        projectRepository.createEntity(username, project);
    }

//    public List<ProjectOverviewDto> getAllProjects(String username) {
//        List<Entity> projectList = projectRepository.getAllEntitiesOnUsername(username);
//        return entityDtoMapper.toProjectOverviewDtoList(projectList);
//    }

    public List<UserInformationDto> getAllTeamMembersFromTeamId(long teamId) {
        return projectRepository.getUsersFromEntityId(teamId);
    }

    public void assignTeamMembersToProject(long projectId, List<String> selectedTeamMembers, UserRole userRole) {
        projectRepository.assignUser(projectId, selectedTeamMembers, userRole);
    }

    public List<UserRole> getAllUserRoles() {
        return projectRepository.getAllUserRoles();
    }

    public void deleteProject(long projectId) {
        projectRepository.deleteEntity(projectId);
    }

    public List<ResourceEntityViewDto> getChildren(long projectId) {
        List<Entity> taskList = projectRepository.getChildren(projectId);
        List<ResourceEntity> downCastedTaskList = toResourceEntityList(taskList);
        return entityDtoMapper.toResourceEntityViewDtoList(downCastedTaskList);
    }

    private List<ResourceEntity> toResourceEntityList(List<Entity> entityList){
        List<ResourceEntity> resourceEntityList = new ArrayList<>();

        for (Entity entity:entityList) {
            resourceEntityList.add((ResourceEntity) entity);
        }

        return resourceEntityList;
    }
}
