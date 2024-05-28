package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.*;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.repository.DepartmentRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository jdbcDepartmentRepository;
    private final EntityDtoMapper entityDtoMapper;

    public DepartmentService(DepartmentRepository jdbcDepartmentRepository, EntityDtoMapper entityDtoMapper) {
        this.jdbcDepartmentRepository = jdbcDepartmentRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

    public EntityViewDto getDepartment(long departmentId) {
        Entity department = jdbcDepartmentRepository.getEntityOnId(departmentId);
        return entityDtoMapper.toEntityViewDto(department);
    }

    @PreAuthorize("@auth.hasOrgansiationAccess(#departmentCreationInfo.parentId()," +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).DEPARTMENT_CREATE)")
    public void createDepartment(EntityCreationDto departmentCreationInfo, String username) {
        Entity departmentToCreate = entityDtoMapper.toEntity(departmentCreationInfo);
        jdbcDepartmentRepository.createEntity(username, departmentToCreate);
    }

//    @PreAuthorize("@auth.hasAccess(#id, T(dev.tolana.projectcalculationtool.enums.Permission).ORGANISATION_READ)")

    //filterObject referes to the current object of the collection, when looping.

    public List<EntityViewDto> getChildren(long parentId) {
        List<Entity> teamList = jdbcDepartmentRepository.getChildren(parentId);
        return entityDtoMapper.toEntityViewDtoList(teamList);
    }

    public void deleteDepartment(long deptId) {
        jdbcDepartmentRepository.deleteEntity(deptId);
    }
    @PreAuthorize("@auth.hasDepartmentAccess(#departmentId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).DEPARTMENT_EDIT)")
    public EntityEditDto getDepartmentToEdit(long departmentId) {
        Entity department = jdbcDepartmentRepository.getEntityOnId(departmentId);
        return entityDtoMapper.toEntityEditDto(department);
    }

    @PreAuthorize("@auth.hasDepartmentAccess(#editInfo.id(), " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).DEPARTMENT_EDIT)")
    public void editDepartment(EntityEditDto editInfo) {
        Entity editedDepartment = entityDtoMapper.toEntity(editInfo);
        jdbcDepartmentRepository.editEntity(editedDepartment);
    }


    //TODO add authorisation check
    public EntityViewDto getParent(long parentId) {
        Entity organisation = jdbcDepartmentRepository.getParent(parentId);
        return entityDtoMapper.toEntityViewDto(organisation);
    }

    public List<UserEntityRoleDto> getUsersFromOrganisationId(long organisationId,long departmentId){
        List<UserEntityRoleDto> scrubbedUsers = new ArrayList<>();

        List<UserEntityRoleDto> users = jdbcDepartmentRepository.getUsersFromParentIdAndEntityId(
                                organisationId,departmentId);

        for(UserEntityRoleDto user : users){
            if(!scrubbedUsers.contains(user)){
                scrubbedUsers.add(user);
            }
        }

        Collections.sort(scrubbedUsers);
        return scrubbedUsers;
    }

    public UserEntityRoleDto getUserFromOrganisationId(String username, long orgId){
        return jdbcDepartmentRepository.getUserFromParentId(username,orgId);
    }

    //add authorisation
    public void assignMemberToDepartment(long deptId, String username){
        jdbcDepartmentRepository.assignMemberToEntity(deptId,username);
    }

    //add authorisation
    public void promoteMemberToAdmin(long deptId, String username){
        jdbcDepartmentRepository.promoteMemberToAdmin(deptId,username);
    }

    //add authorisation
    public void kickMemberFromDepartment(long deptId, String username){
        jdbcDepartmentRepository.kickMember(deptId,username);
    }
}
