package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.dto.ProjectOverviewDto;
import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EntityDtoMapper {

    public Entity toEntity(EntityCreationDto entityCreationDto) {
        return new Entity(
                -1,
                entityCreationDto.entityName(),
                entityCreationDto.description(),
                LocalDateTime.now(),
                false
        );
    }

    public List<Organisation> toOrganisationList(List<Entity> entityList) {
        List<Organisation> organisationList = new ArrayList<>();
        for (Entity entity : entityList) {
            Organisation organisation = new Organisation(
                    entity.getId(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getDateCreated(),
                    entity.isArchived()
            );
            organisationList.add(organisation);
        }
        return organisationList;
    }

    public TaskDto convertToTaskDto(Task task) {

        String taskName = task.getName();
        String taskDescription = task.getDescription();
        long projectId = task.getProjectId();
        LocalDateTime deadline = task.getDeadline();
        int estimatedHours = task.getEstimatedHours();
        Status status = task.getStatus();
        long parentId = task.getParentId();
        long taskId = task.getId();

        return new TaskDto(taskName, taskDescription, projectId, deadline, estimatedHours, status, parentId, taskId);
    }

    public EntityViewDto convertToEntityViewDto(Entity entity) {

        if (entity instanceof Organisation organisation) {
            return new EntityViewDto(
                    organisation.getName(),
                    organisation.getDescription(),
                    organisation.getId(),
                    0,
                    organisation.isArchived());
        }

        if (entity instanceof Department department) {
            return new EntityViewDto(
                    department.getName(),
                    department.getDescription(),
                    department.getId(),
                    department.getOrganisationId(),
                    department.isArchived());
        }

        if (entity instanceof Team team) {
            return new EntityViewDto(
                    team.getName(),
                    team.getDescription(),
                    team.getId(),
                    team.getDepartmentId(),
                    team.isArchived());
        }

        return null;
    }

    public List<EntityViewDto> convertToEntityViewDtoList(List<Entity> entityList) {
        List<EntityViewDto> entityViewDtoList = new ArrayList<>();
        EntityViewDto entityViewDto;

        for (Entity entity:entityList) {
            if (entity instanceof Organisation organisation) {
                  entityViewDto = new EntityViewDto(
                        organisation.getName(),
                        organisation.getDescription(),
                        organisation.getId(),
                        0,
                          organisation.isArchived());

                 entityViewDtoList.add(entityViewDto);
            }

            if (entity instanceof Department department) {
                 entityViewDto = new EntityViewDto(
                        department.getName(),
                        department.getDescription(),
                        department.getId(),
                        department.getOrganisationId(),
                         department.isArchived());

                entityViewDtoList.add(entityViewDto);
            }

            if (entity instanceof Team team) {
                entityViewDto = new EntityViewDto(
                        team.getName(),
                        team.getDescription(),
                        team.getId(),
                        team.getDepartmentId(),
                        team.isArchived());

                entityViewDtoList.add(entityViewDto);
            }
        }

        return entityViewDtoList;
    }

    public Entity convertToEntity(TaskDto taskDto) {
        return new Task(
                taskDto.taskId(),
                taskDto.taskName(),
                taskDto.taskDescription(),
                taskDto.deadline(),
                taskDto.status(),
                taskDto.parentId(),
                taskDto.projectId(),
                taskDto.estimatedHours()
        );
    }

    public List<TaskDto> toTaskDtoList(List<Entity> taskList) {
        List<TaskDto> taskDtoList = new ArrayList<>();

        for (Entity task : taskList) {
            TaskDto taskDto = new TaskDto(
                    task.getName(),
                    task.getDescription(),
                    ((Task) task).getProjectId(),
                    ((ResourceEntity) task).getDeadline(),
                    ((Task) task).getEstimatedHours(),
                    ((ResourceEntity) task).getStatus(),
                    ((ResourceEntity)task).getParentId(),
                    task.getId()
            );
            taskDtoList.add(taskDto);
        }

        return taskDtoList;
    }

    public List<ProjectOverviewDto> toProjectOverviewDtoList(List<Entity> projectList) {
        List<ProjectOverviewDto> projectOverviewDtoList = new ArrayList<>();

        for (Entity project : projectList) {
            ProjectOverviewDto projectOverviewDto = new ProjectOverviewDto(
                    project.getName(),
                    ((ResourceEntity)project).getDeadline(),
                    ((Project)project).getAllottedHours(),
                    ((ResourceEntity)project).getStatus(),
                    project.getId()
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
                project.getId()
        );
    }
}
