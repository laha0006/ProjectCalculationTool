package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;

import java.util.List;

public interface TaskRepository extends ResourceEntityCrudOperations{
    @Override
    List<UserEntityRoleDto> getUsersFromEntityId(long entityId);

    void removeSelfFromTask(long taskId);
}
