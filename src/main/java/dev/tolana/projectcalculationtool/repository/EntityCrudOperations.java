package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;

import java.util.List;
import java.util.Set;

public interface EntityCrudOperations {

    boolean createEntity(String username, Entity entity);

    Entity getEntityOnId(long id);
    List<Entity> getAllEntitiesOnUsername(String username);

    List<Entity> getAllEntitiesOnId(long entityId);

    boolean editEntity(Entity entity);

    boolean deleteEntity(long entityId);

    boolean inviteToEntity(String inviteeUsername);

    boolean archiveEntity(long entityId, boolean isArchived);

    boolean assignUser(long entityId, List<String> username, UserRole role);

    List<UserInformationDto> getUsersFromEntityId(long entityId);

    List<UserRole> getAllUserRoles();
}
