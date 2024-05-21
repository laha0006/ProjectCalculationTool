package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;

import java.util.List;

public interface DepartmentRepository extends EntityCrudOperations{

    List<UserEntityRoleDto> getUsersFromOrganisationId(long organisationId, long departmentId);

    UserEntityRoleDto getUserFromOrganisationId(String username, long organisationId);

    void assignMemberToDepartment(long deptId, String username);
}
