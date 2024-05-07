package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Organisation;

import java.util.List;

public interface OrganisationRepository {
    List<Organisation> getOrganisationsByUser(String username);

    void createOrganisation(String username, String organisationName, String organisationDescription);
}
