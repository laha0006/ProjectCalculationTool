package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.model.Project;

import java.util.List;

public interface DashboardRepository {

    List<UserEntityRoleDto> getUserEntityRoleListOnUsername(String username);

    int addProject(Project project);
}
