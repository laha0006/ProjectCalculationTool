package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.mapper.ProjectDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final ProjectDtoMapper projectDtoMapper;
    private final EntityDtoMapper entityDtoMapper;

    public TeamService(TeamRepository teamRepository, ProjectDtoMapper projectDtoMapper, EntityDtoMapper entityDtoMapper) {
        this.teamRepository = teamRepository;
        this.projectDtoMapper = projectDtoMapper;
        this.entityDtoMapper = entityDtoMapper;
    }

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

    public List<ProjectViewDto> getChildren(long teamId) {
        List<Entity> projectList = teamRepository.getChildren(teamId);
        return projectDtoMapper.toProjectViewDtoList(projectList);
    }

    public void deleteTeam(long teamId) {
        teamRepository.deleteEntity(teamId);
    }

    public EntityViewDto getParent(long parentId) {
        Entity organisation = teamRepository.getParent(parentId);
        return entityDtoMapper.toEntityViewDto(organisation);
    }


    public List<UserEntityRoleDto> getUsersFromDepartmentId(long departmentId,long teamId){
        List<UserEntityRoleDto> scrubbedUsers = new ArrayList<>();

        List<UserEntityRoleDto> users = teamRepository.getUsersFromParentIdAndEntityId(
                departmentId,teamId);

        for(UserEntityRoleDto user : users){
            if(!scrubbedUsers.contains(user)){
                scrubbedUsers.add(user);
            }
        }

        Collections.sort(scrubbedUsers);
        return scrubbedUsers;
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
