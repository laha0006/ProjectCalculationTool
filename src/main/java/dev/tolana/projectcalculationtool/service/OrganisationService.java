package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Invitation;
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
        List<EntityViewDto> organisations = entityDtoMapper.toEntityViewDtoList(entityList);

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
    public EntityViewDto getOrganisation(long organisationId) {
        Entity organisation = organisationRepository.getEntityOnId(organisationId);
        return entityDtoMapper.toEntityViewDto(organisation);
    }

    @PreAuthorize("@auth.hasOrgansiationAccess(#orgId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).ORGANISATION_INVITE)")
    public List<Invitation> getAllOutstandingInvitations(long orgId) {
        return organisationRepository.getAllOutstandingInvitations(orgId);
    }

    public void deleteOrganisation(long organisationId) {
        organisationRepository.deleteEntity(organisationId);
    }

    public List<EntityViewDto> getChildren(long organisationId) {
        List<Entity> departments = organisationRepository.getChildren(organisationId);
        return entityDtoMapper.toEntityViewDtoList(departments);
    }
}
