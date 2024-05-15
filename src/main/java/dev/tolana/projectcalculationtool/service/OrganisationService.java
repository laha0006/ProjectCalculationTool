package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.repository.EntityCrudOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganisationService {

    private final EntityCrudOperations organisationRepository;
    private final EntityDtoMapper entityDtoMapper;

    public OrganisationService(EntityCrudOperations organisationRepository, EntityDtoMapper entityDtoMapper) {
        this.organisationRepository = organisationRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

    public List<Organisation> getOrganisationsByUser(String username) {
        List<Entity> entityList = organisationRepository.getAllEntitiesOnUsername(username);
        return entityDtoMapper.toOrganisationList(entityList);
    }

    public List<Organisation> getNotArchivedOrganisationsByUser(String username) {
        List<Entity> entityList = organisationRepository.getAllEntitiesOnUsername(username);
        List<Organisation> organisations = entityDtoMapper.toOrganisationList(entityList);
        List<Organisation> notArchivedOrganisations = new ArrayList<>();

        for (Organisation organisation : organisations) {
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
