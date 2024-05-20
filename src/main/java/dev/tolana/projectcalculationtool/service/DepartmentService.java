package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.repository.DepartmentRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
    @PostFilter("@auth.hasDepartmentAccess(filterObject.id, T(dev.tolana.projectcalculationtool.enums.Permission).DEPARTMENT_READ)")
    public List<EntityViewDto> getAll(long departmentId) {
        List<Entity> teamList = jdbcDepartmentRepository.getAllEntitiesOnId(departmentId);
        return entityDtoMapper.toEntityViewDtoList(teamList);
    }

    public List<EntityViewDto> getChildren(long parentId) {
        List<Entity> teamList = jdbcDepartmentRepository.getChildren(parentId);
        return entityDtoMapper.toEntityViewDtoList(teamList);
    }

    public void deleteDepartment(long deptId) {
        jdbcDepartmentRepository.deleteEntity(deptId);
    }

    //TODO add authorisation check
    public EntityViewDto getParent(long parentId) {
        Entity organisation = jdbcDepartmentRepository.getParent(parentId);
        return entityDtoMapper.toEntityViewDto(organisation);
    }

    public List<UserEntityRoleDto> getUsersFromOrganisationId(long organisationId){
        return jdbcDepartmentRepository.getUsersFromOrganisationId(organisationId);
    }
}
