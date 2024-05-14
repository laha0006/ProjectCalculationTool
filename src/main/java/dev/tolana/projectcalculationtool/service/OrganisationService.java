package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.model.Organisation;
import dev.tolana.projectcalculationtool.repository.OrganisationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    public List<Organisation> getOrganisationsByUser(String username) {
        return organisationRepository.getOrganisationsByUser(username);
    }

    public List<Organisation> getNotArchivedOrganisationsByUser(String username) {
        List<Organisation> organisations = organisationRepository.getOrganisationsByUser(username);
        List<Organisation> notArchivedOrganisations = new ArrayList<>();
        for (Organisation organisation : organisations) {
            if (!organisation.isArchived()) {
                notArchivedOrganisations.add(organisation);}
        }
        return notArchivedOrganisations;
    }

    public void createOrganisation(String username, String organisationName, String organisationDescription) {
    organisationRepository.createOrganisation(username, organisationName, organisationDescription);
    }

    public Organisation getOrganisationsById(long id) {
        return organisationRepository.getOrganisationById(id);
    }
}
