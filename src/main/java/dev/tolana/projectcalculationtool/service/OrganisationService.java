package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.repository.EntityCrudOperations;
import dev.tolana.projectcalculationtool.repository.OrganisationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final EntityDtoMapper entityDtoMapper;

    public OrganisationService(OrganisationRepository organisationRepository, EntityDtoMapper entityDtoMapper) {
        this.organisationRepository = organisationRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

    public List<EntityViewDto> getNotArchivedOrganisationsByUser(String username) {
        List<EntityViewDto> notArchivedOrganisations = new ArrayList<>();

        List<Entity> entityList = organisationRepository.getAllEntitiesOnUsername(username);
        List<EntityViewDto> organisations = entityDtoMapper.convertToEntityViewDtoList(entityList);

        for (EntityViewDto organisation : organisations) {
            if (!organisation.isArchived()) {
                notArchivedOrganisations.add(organisation);
            }
        }
        return notArchivedOrganisations;
    }

    public void createOrganisation(String username, EntityCreationDto creationInfo) {
        Entity organisation = entityDtoMapper.toEntity(creationInfo);
        organisationRepository.createEntity(username, organisation);
    }
    //spring expression language...
    @PreAuthorize("@auth.hasOrgansiationAccess(#organisationId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).ORGANISATION_READ )")
    public Entity getOrganisationsById(long organisationId) {
        return organisationRepository.getEntityOnId(organisationId);
    }
}
