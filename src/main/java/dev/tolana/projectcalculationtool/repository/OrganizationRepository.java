package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Organization;

import java.util.List;

public interface OrganizationRepository {
    List<Organization> getOrganizationsByUser(String username);
}
