package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.EntityCreationDto;
import dev.tolana.projectcalculationtool.dto.EntityEditDto;
import dev.tolana.projectcalculationtool.dto.EntityViewDto;
import dev.tolana.projectcalculationtool.dto.ResourceEntityViewDto;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.ResourceEntity;
import dev.tolana.projectcalculationtool.repository.TeamRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("@auth.hasTeamAccess(#teamId," +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TEAM_READ)")
    public EntityViewDto getTeam(long teamId){
        Entity team = teamRepository.getEntityOnId(teamId);
        return entityDtoMapper.toEntityViewDto(team);
    }
    @PreAuthorize("@auth.hasTeamAccess(#teamId," +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TEAM_EDIT)")
    public EntityEditDto getTeamToEdit(long teamId){
        Entity team = teamRepository.getEntityOnId(teamId);
        return entityDtoMapper.toEntityEditDto(team);
    }
    @PreAuthorize("@auth.hasTeamAccess(#editDto.id()," +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TEAM_READ)")
    public void editTeam(EntityEditDto editDto) {
        Entity teamToEdit = entityDtoMapper.toEntity(editDto);
        teamRepository.editEntity(teamToEdit);
    }
    @PreAuthorize("@auth.hasDepartmentAccess(#teamCreationInfo.parentId()," +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TEAM_CREATE)")
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
}
