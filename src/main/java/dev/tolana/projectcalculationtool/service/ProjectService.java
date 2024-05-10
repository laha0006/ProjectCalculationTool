package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.ProjectOverviewDto;
import dev.tolana.projectcalculationtool.mapper.ProjectDtoMapper;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectDtoMapper projectDtoMapper;

    public ProjectService(ProjectRepository projectRepository, ProjectDtoMapper projectDtoMapper) {
        this.projectRepository = projectRepository;
        this.projectDtoMapper = projectDtoMapper;
    }

    public int addProject(Project project) {
        return projectRepository.addProject(project);
    }

    public List<ProjectOverviewDto> getAllProjects(String username) {
        List<Project> projectList = projectRepository.getAllProjectsOnUsername(username);
        return projectDtoMapper.toProjectOverviewDtoList(projectList);
    }

    public long getTeamIdFromUsername(String username) {
        return projectRepository.getTeamIdFromUsername(username);
    }
}
