package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.repository.DashboardRepository;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public int addProject(Project project){
        return projectRepository.addProject(project);
    }
}
