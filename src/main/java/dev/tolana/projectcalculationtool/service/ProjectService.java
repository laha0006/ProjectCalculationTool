package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.mapper.ProjectDtoMapper;
import dev.tolana.projectcalculationtool.mapper.TaskDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CalculationService calculationService;
    private final ProjectDtoMapper projectDtoMapper;
    private final TaskDtoMapper taskDtoMapper;

    public ProjectService(ProjectRepository projectRepository,CalculationService calculationService, ProjectDtoMapper projectDtoMapper, TaskDtoMapper taskDtoMapper) {
        this.projectRepository = projectRepository;
        this.calculationService = calculationService;
        this.projectDtoMapper = projectDtoMapper;
        this.taskDtoMapper = taskDtoMapper;
    }

    public void createProject(String username, ProjectCreationDto project) {
        Entity newProject = projectDtoMapper.toEntity(project);
        projectRepository.createEntity(username, newProject);
    }

    public ProjectViewDto getProjectToView(long projectId) {
        Entity project = projectRepository.getEntityOnId(projectId);
        return projectDtoMapper.toProjectViewDto(project);
    }

    public List<ProjectViewDto> getSubProjects(long projectId) {
        List<Project> subProjects = projectRepository.getSubProjects(projectId);
        List<Entity> upCastedSubProjects = fromProjecToEntityList(subProjects);
        return projectDtoMapper.toProjectViewDtoList(upCastedSubProjects);
    }

    public ProjectStatsDto getProjectStats(long projectId) {
        return calculationService.getProjectStats(projectId);
    }

    public List<TaskViewDto> getTasks(long projectId) {
        List<Entity> taskList = projectRepository.getChildren(projectId);
        return taskDtoMapper.toTaskDtoViewList(taskList);
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

    private List<Entity> fromProjecToEntityList(List<Project> entityList){
        return new ArrayList<>(entityList);
    }

    public List<Status> getStatusList() {
        return projectRepository.getStatusList();
    }

    public void editProject(ProjectEditDto projectToEdit) {
        Entity project = projectDtoMapper.toEntity(projectToEdit);
        projectRepository.editEntity(project);
    }

    public ProjectEditDto getProjectToEdit(long projectId) {
        Entity projectToEdit = projectRepository.getEntityOnId(projectId);
        return projectDtoMapper.toProjectEditDto(projectToEdit);
    }
}
