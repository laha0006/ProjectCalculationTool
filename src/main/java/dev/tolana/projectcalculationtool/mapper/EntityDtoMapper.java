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
}
