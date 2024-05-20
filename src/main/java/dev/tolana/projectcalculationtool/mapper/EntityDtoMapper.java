package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EntityDtoMapper {

    public Entity toEntity(EntityCreationDto entityCreationDto) {

        switch (entityCreationDto.entityType()) {
            case ORGANISATION -> {
                return new Organisation(
                        -1,
                        entityCreationDto.entityName(),
                        entityCreationDto.description(),
                        LocalDateTime.now(),
                        false
                );
            }
            case DEPARTMENT -> {
                return new Department(
                        -1,
                        entityCreationDto.entityName(),
                        entityCreationDto.description(),
                        LocalDateTime.now(),
                        false,
                        entityCreationDto.parentId()
                );
            }
            case TEAM -> {
                return new Team(
                        -1,
                        entityCreationDto.entityName(),
                        entityCreationDto.description(),
                        LocalDateTime.now(),
                        false,
                        entityCreationDto.parentId()
                );
            }
            default -> {
                return null;
            }
        }
    }

//    public Entity toEntity(ResourceEntityCreationDto reCreationDto) {
//
//        switch (reCreationDto.entityType()) {
//            case PROJECT -> {return new Project(
//                    -1,
//                    reCreationDto.resourceEntityName(),
//                    reCreationDto.description(),
//                    LocalDateTime.now(),
//                    false,
//                    reCreationDto.deadline(),
//                    Status.IN_PROGRESS,
//                    reCreationDto.parentId(),
//                    reCreationDto.teamId(),
//                    -1
//            );}
//
//            case TASK -> {return new Task(
//                    -1,
//                    reCreationDto.resourceEntityName(),
//                    reCreationDto.description(),
//                    LocalDateTime.now(),
//                    false,
//                    reCreationDto.deadline(),
//                    Status.TODO,
//                    reCreationDto.parentId(),
//                    reCreationDto.projectId(),
//                    reCreationDto.estimatedHours(),
//                    -1
//            );}
//            default -> {
//                return null;
//            }
//        }
//    }

    public Entity toEntity(ProjectCreationDto projectCreationDto) {
        return new Project(
                -1,
                projectCreationDto.projectName(),
                projectCreationDto.description(),
                LocalDateTime.now(),
                false,
                projectCreationDto.deadline(),
                Status.IN_PROGRESS,
                projectCreationDto.parentId(),
                projectCreationDto.teamId(),
                projectCreationDto.allottedHours()
        );
    }

    public Entity toEntity(TaskDto taskDto) {
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

    public Entity toEntity(EntityEditDto editDto) {
        return new Entity(
                editDto.id(),
                editDto.name(),
                editDto.description()
        );
    }


    public EntityViewDto toEntityViewDto(Entity entity) {

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

    public EntityEditDto toEntityEditDto(Entity entity) {
        return new EntityEditDto(
                entity.getName(),
                entity.getDescription(),
                entity.getId()
        );
    }

    public List<EntityViewDto> toEntityViewDtoList(List<Entity> entityList) {
        List<EntityViewDto> entityViewDtoList = new ArrayList<>();
        EntityViewDto entityViewDto;

        for (Entity entity : entityList) {
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

    public ResourceEntityViewDto toResourceEntityViewDto(Entity entity) {

        if (entity instanceof Project project) {
            return new ResourceEntityViewDto(
                    project.getName(),
                    project.getDescription(),
                    project.getId(),
                    project.getParentId(),
                    project.getTeamId(),
                    -1,
                    project.getDeadline(),
                    -1, //TODO add estimatedHours to Project to calculate hours to finish whole project
                    -1,
                    project.getAllottedHours(),
                    project.getStatus()
            );
        }

        if (entity instanceof Task task) {
            return new ResourceEntityViewDto(
                    task.getName(),
                    task.getDescription(),
                    task.getId(),
                    task.getParentId(),
                    -1,
                    task.getProjectId(),
                    task.getDeadline(),
                    task.getEstimatedHours(),
                    task.getActualHours(),
                    -1,
                    task.getStatus()
            );
        }

        return null;
    }

    public List<ResourceEntityViewDto> toResourceEntityViewDtoList(List<ResourceEntity> entityList) {
        List<ResourceEntityViewDto> entityViewDtoList = new ArrayList<>();
        ResourceEntityViewDto resourceEntityViewDto;

        for (ResourceEntity resourceEntity : entityList) {
            if (resourceEntity instanceof Project project) {
                resourceEntityViewDto = new ResourceEntityViewDto(
                        project.getName(),
                        project.getDescription(),
                        project.getId(),
                        project.getParentId(),
                        project.getTeamId(),
                        -1,
                        project.getDeadline(),
                        -1, //TODO add estimatedHours to Project to calculate hours to finish whole project
                        -1,
                        project.getAllottedHours(),
                        project.getStatus()
                );

                entityViewDtoList.add(resourceEntityViewDto);
            }

            if (resourceEntity instanceof Task task) {
                resourceEntityViewDto = new ResourceEntityViewDto(
                        task.getName(),
                        task.getDescription(),
                        task.getId(),
                        task.getParentId(), //used for CRUD on subtasks
                        -1,
                        task.getProjectId(),
                        task.getDeadline(),
                        task.getEstimatedHours(),
                        task.getActualHours(),
                        -1,
                        task.getStatus()
                );

                entityViewDtoList.add(resourceEntityViewDto);
            }
        }
        return entityViewDtoList;
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
                    ((ResourceEntity) task).getParentId(),
                    task.getId()
            );
            taskDtoList.add(taskDto);
        }

        return taskDtoList;
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
}
