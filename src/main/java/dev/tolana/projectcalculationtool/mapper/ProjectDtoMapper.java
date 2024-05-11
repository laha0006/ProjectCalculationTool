package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.ProjectOverviewDto;
import dev.tolana.projectcalculationtool.model.Project;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectDtoMapper {

    public List<ProjectOverviewDto> toProjectOverviewDtoList(List<Project> projectList) {
        List<ProjectOverviewDto> projectOverviewDtoList = new ArrayList<>();

        for (Project project : projectList) {
            ProjectOverviewDto projectOverviewDto = new ProjectOverviewDto(
                    project.getName(),
                    project.getDeadline(),
                    project.getAllottedHours(),
                    project.getStatus(),
                    project.getProjectId()
            );
            projectOverviewDtoList.add(projectOverviewDto);
        }

        return projectOverviewDtoList;
    }

    public ProjectOverviewDto toProjectOverviewDto(Project project) {
        return new ProjectOverviewDto(
                project.getName(),
                project.getDeadline(),
                project.getAllottedHours(),
                project.getStatus(),
                project.getProjectId()
        );
    }
}
