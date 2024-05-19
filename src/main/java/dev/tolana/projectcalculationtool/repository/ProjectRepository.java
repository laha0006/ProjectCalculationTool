package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserInformationDto;
import dev.tolana.projectcalculationtool.model.Project;

import java.util.List;

public interface ProjectRepository extends ResourceEntityCrudOperations{
    List<Project> getSubProjects(long projectId);
}
