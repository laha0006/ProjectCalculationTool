package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.model.Invitation;

import java.util.List;

public interface OrganisationRepository extends EntityCrudOperations{
    List<Invitation> getAllOutstandingInvitations(long organisationId);

    List<UserEntityRoleDto> getUsersFromOrganisationId(long entityId);
}
