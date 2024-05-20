package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.enums.Status;
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
    private final CalculationService calculationService;
    private final EntityDtoMapper entityDtoMapper;

    public ProjectService(ProjectRepository projectRepository,CalculationService calculationService, EntityDtoMapper entityDtoMapper) {
        this.projectRepository = projectRepository;
        this.calculationService = calculationService;
        this.entityDtoMapper = entityDtoMapper;
    }

    public void createProject(String username, ProjectCreationDto project) {
        Entity newProject = entityDtoMapper.toEntity(project);
        projectRepository.createEntity(username, newProject);
    }

    public ResourceEntityViewDto getProject(long projectId) {
        Entity project = projectRepository.getEntityOnId(projectId);
        return entityDtoMapper.toResourceEntityViewDto(project);
    }

    public List<ResourceEntityViewDto> getSubProjects(long projectId) {
        List<Project> subProjects = projectRepository.getSubProjects(projectId);
        List<ResourceEntity> upCastedSubProjects = fromProjectoResourceEntityList(subProjects);

        return entityDtoMapper.toResourceEntityViewDtoList(upCastedSubProjects);
    }

    public ProjectStatsDto getProjectStats(long projectId) {
        return calculationService.getProjectStats(projectId);
    }

    public List<ResourceEntityViewDto> getTasks(long projectId) {
        List<Entity> taskList = projectRepository.getChildren(projectId);
        List<ResourceEntity> downCastedTaskList = toResourceEntityList(taskList);

        return entityDtoMapper.toResourceEntityViewDtoList(downCastedTaskList);
    }

    public List<UserInformationDto> getAllTeamMembersFromTeamId(long teamId) {
        return projectRepository.getUsersFromEntityId(teamId);
    }

    public List<UserRole> getAllUserRoles() {
        return projectRepository.getAllUserRoles();
    }

    public void assignTeamMembersToProject(long projectId, List<String> selectedTeamMembers, UserRole userRole) {
        projectRepository.assignUser(projectId, selectedTeamMembers, userRole);
    }

    public void deleteProject(long projectId) {
        projectRepository.deleteEntity(projectId);
    }

    private List<ResourceEntity> toResourceEntityList(List<Entity> entityList){
        List<ResourceEntity> resourceEntityList = new ArrayList<>();

        for (Entity entity:entityList) {
            resourceEntityList.add((ResourceEntity) entity);
        }

        return resourceEntityList;
    }

    private List<ResourceEntity> fromProjectoResourceEntityList(List<Project> entityList){
        return new ArrayList<>(entityList);
    }

    public List<Status> getStatusList() {
        return projectRepository.getStatusList();
    }

    public void editProject(ResourceEntityViewDto projectToEdit) {
        Entity project = entityDtoMapper.toEntity(projectToEdit);
        projectRepository.editEntity(project);
    }
}
