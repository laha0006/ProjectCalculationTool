package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Invitation;

import java.util.List;

public interface OrganisationRepository extends EntityCrudOperations{
    List<Invitation> getAllOutstandingInvitations(long organisationId);
}
