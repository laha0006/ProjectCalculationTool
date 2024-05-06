package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.model.Organization;
import dev.tolana.projectcalculationtool.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public List<Organization> getOrganizationsByUser(String username) {
        return organizationRepository.getOrganizationsByUser(username);
    }

    public List<Organization> getNotArchivedOrganizationsByUser(String username) {
        List<Organization> organizations = organizationRepository.getOrganizationsByUser(username);
        List<Organization> notArchivedOrganizations = new ArrayList<>();
        for (Organization organization : organizations) {
            if (!organization.isArchived()) {notArchivedOrganizations.add(organization);}
        }
        return notArchivedOrganizations;
    }

}
