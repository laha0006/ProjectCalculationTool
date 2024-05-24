package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.ResourceEntity;
import dev.tolana.projectcalculationtool.repository.TeamRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final EntityDtoMapper entityDtoMapper;

    public TeamService(TeamRepository teamRepository, EntityDtoMapper entityDtoMapper) {
        this.teamRepository = teamRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

/* --old--
    public List<Team> getTeamsByUser(String username) {
        return teamRepository.getTeamsByUser(username);
    }
 */

    public EntityViewDto getTeam(long teamId){
        Entity team = teamRepository.getEntityOnId(teamId);
        return entityDtoMapper.toEntityViewDto(team);
    }

    public EntityEditDto getTeamToEdit(long teamId){
        Entity team = teamRepository.getEntityOnId(teamId);
        return entityDtoMapper.toEntityEditDto(team);
    }

    public void editTeam(EntityEditDto editDto) {
        Entity teamToEdit = entityDtoMapper.toEntity(editDto);
        teamRepository.editEntity(teamToEdit);
    }

    public void createTeam(EntityCreationDto teamCreationInfo, String username) {
        Entity teamToCreate = entityDtoMapper.toEntity(teamCreationInfo);
        teamRepository.createEntity(username, teamToCreate);
    }

    @PostFilter("@auth.hasDepartmentAccess(filterObject.id, T(dev.tolana.projectcalculationtool.enums.Permission).DEPARTMENT_READ)")
    public List<EntityViewDto> getAll(long departmentId) {
        List<Entity> teamList = teamRepository.getAllEntitiesOnId(departmentId);
        return entityDtoMapper.toEntityViewDtoList(teamList);
    }

    public List<ResourceEntityViewDto> getAllChildren(long teamId) {
        List<Entity> projectList = teamRepository.getChildren(teamId);
        List<ResourceEntity> downCastedProjectList = toResourceEntityList(projectList);
        return entityDtoMapper.toResourceEntityViewDtoList(downCastedProjectList);
    }

    private List<ResourceEntity> toResourceEntityList(List<Entity> entityList){
        List<ResourceEntity> resourceEntityList = new ArrayList<>();

        for (Entity entity:entityList) {
            resourceEntityList.add((ResourceEntity) entity);
        }

        return resourceEntityList;
    }

    public void deleteTeam(long teamId) {
        teamRepository.deleteEntity(teamId);
    }

    public EntityViewDto getParent(long parentId) {
        Entity organisation = teamRepository.getParent(parentId);
        return entityDtoMapper.toEntityViewDto(organisation);
    }


    public List<UserEntityRoleDto> getUsersFromDepartmentId(long departmentId,long teamId){
        return teamRepository.getUsersFromParentIdAndEntityId(departmentId,teamId);
    }

    public UserEntityRoleDto getUserFromDepartmentId(String username, long deptId){
        return teamRepository.getUserFromParentId(username,deptId);
    }

    //add authorisation
    public void assignMemberToTeam(long teamId, String username){
        teamRepository.assignMemberToEntity(teamId,username);
    }

    //add authorisation
    public void promoteMemberToAdmin(long teamId, String username){
        teamRepository.promoteMemberToAdmin(teamId,username);
    }

    //add authorisation
    public void kickMemberFromTeam(long teamId, String username){
        teamRepository.kickMember(teamId,username);
    }
}
