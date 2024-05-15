package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Organisation;
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
                true
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
}
