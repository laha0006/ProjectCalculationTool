package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectDtoMapper {
    public ProjectViewDto toProjectViewDto(Entity entity) {
        return new ProjectViewDto(
                entity.getId(),
                ((Project)entity).getParentId(),
                entity.getName(),
                entity.getDescription(),
                ((Project) entity).getDeadline(),
                ((Project) entity).getStatus()
        );
    }

    public List<ProjectViewDto> toProjectViewDtoList(List<Entity> projectList) {
        List<ProjectViewDto> projectViewDtoList = new ArrayList<>();

        for (Entity entity : projectList) {
            projectViewDtoList.add(toProjectViewDto(entity));
        }

        return projectViewDtoList;
    }

    public ProjectEditDto toProjectEditDto(Entity project) {
        return new ProjectEditDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                ((Project)project).getDeadline(),
                ((Project) project).getAllottedHours(),
                ((Project) project).getStatus()
        );
    }

    public Entity toEntity(ProjectCreationDto projectToCreate) {
        return new Project(
                projectToCreate.projectName(),
                projectToCreate.description(),
                projectToCreate.parentId(),
                projectToCreate.teamId(),
                projectToCreate.deadline(),
                projectToCreate.allottedHours()
        );
    }

    public Entity toEntity(ProjectEditDto projectToEdit) {
        return new Project(
                projectToEdit.id(),
                projectToEdit.projectName(),
                projectToEdit.description(),
                projectToEdit.deadline(),
                projectToEdit.allottedHours(),
                projectToEdit.status()
        );
    }
}
