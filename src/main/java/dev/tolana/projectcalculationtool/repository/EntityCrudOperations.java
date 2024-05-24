package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.enums.UserRole;
import dev.tolana.projectcalculationtool.model.Entity;

import java.util.List;

public interface EntityCrudOperations {

    boolean createEntity(String username, Entity entity);

    Entity getEntityOnId(long id);
    List<Entity> getAllEntitiesOnUsername(String username);

    List<Entity> getChildren(long entityId);

    Entity getParent(long parentId);

    boolean editEntity(Entity entity);

    boolean deleteEntity(long entityId);

    boolean inviteToEntity(String inviteeUsername);

    boolean archiveEntity(long entityId, boolean isArchived);

    boolean assignUser(long entityId, List<String> username, UserRole role);

    List<UserInformationDto> getUsersFromEntityId(long entityId);

    List<UserEntityRoleDto> getUsersFromParentIdAndEntityId(long parentId, long entityId);

    UserEntityRoleDto getUserFromParentId(String username, long parentId);

    List<UserRole> getAllUserRoles();

    void assignMemberToEntity(long entityId, String username);

    void promoteMemberToAdmin(long entityId, String username);

    void kickMember(long entityId, String username);
}
