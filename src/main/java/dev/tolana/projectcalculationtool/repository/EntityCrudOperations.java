package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Entity;

import java.util.List;

public interface EntityCrudOperations {

    boolean createEntity(String username, Entity entity);

    Entity getEntityOnId(long id);
    List<Entity> getAllEntitiesOnUsername(String username);

    boolean editEntity(Entity entity);

    boolean deleteEntity(long entityId);

    boolean inviteToEntity(String inviteeUsername);

    boolean archiveEntity(long entityId, boolean isArchived);
}
