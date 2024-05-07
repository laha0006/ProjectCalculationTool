package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.model.Project;

import java.util.List;

public interface ProjectRepository {

    int addProject(Project project);
}
