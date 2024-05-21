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

    public Entity toEntity(ResourceEntityViewDto resourceEntityViewDto) {
        if (resourceEntityViewDto.projectId() == 0) { //if it doesnt have a projectId it means its a task, otherwise its a project
            return new Project(
                    resourceEntityViewDto.id(),
                    resourceEntityViewDto.resourceEntityName(),
                    resourceEntityViewDto.description(),
                    LocalDateTime.now(),
                    false,
                    resourceEntityViewDto.deadline(),
                    resourceEntityViewDto.status(),
                    resourceEntityViewDto.parentId(),
                    resourceEntityViewDto.teamId(),
                    resourceEntityViewDto.allottedHours()
            );

        } else {
            return new Task(
                    resourceEntityViewDto.id(),
                    resourceEntityViewDto.resourceEntityName(),
                    resourceEntityViewDto.description(),
                    LocalDateTime.now(),
                    false,
                    resourceEntityViewDto.deadline(),
                    resourceEntityViewDto.status(),
                    resourceEntityViewDto.parentId(),
                    resourceEntityViewDto.projectId(),
                    resourceEntityViewDto.estimatedHours(),
                    resourceEntityViewDto.actualHours()
            );
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
                    0,
                    project.getDeadline(),
                    0, //TODO add estimatedHours to Project to calculate hours to finish whole project
                    0,
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
                        0,
                        project.getDeadline(),
                        0, //TODO add estimatedHours to Project to calculate hours to finish whole project
                        0,
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
                        0,
                        task.getProjectId(),
                        task.getDeadline(),
                        task.getEstimatedHours(),
                        task.getActualHours(),
                        0,
                        task.getStatus()
                );

                entityViewDtoList.add(resourceEntityViewDto);
            }
        }
        return entityViewDtoList;
    }
}
