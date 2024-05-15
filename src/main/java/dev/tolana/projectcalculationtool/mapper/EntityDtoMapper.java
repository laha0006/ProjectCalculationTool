package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.EntityFormDto;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Organisation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EntityDtoMapper {

    public Entity toEntity(EntityFormDto entityFormDto) {
        return new Entity(
                -1,
                entityFormDto.entityName(),
                entityFormDto.description(),
                LocalDateTime.now(),
                true
        );
    }

}
