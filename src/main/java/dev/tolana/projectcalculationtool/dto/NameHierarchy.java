package dev.tolana.projectcalculationtool.dto;

public record NameHierarchy(String taskName,
                            String projectName,
                            String teamName,
                            String departmentName,
                            String organisationName,
                            String parentProjectName,
                            String parentTaskName
                            ) {
}
