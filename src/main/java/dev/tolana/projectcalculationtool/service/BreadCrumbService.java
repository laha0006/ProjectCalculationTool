package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.BreadCrumbDto;
import dev.tolana.projectcalculationtool.dto.BreadCrumbItmDto;
import dev.tolana.projectcalculationtool.dto.NameHierarchy;
import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.repository.BreadCrumbRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BreadCrumbService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private BreadCrumbRepository breadCrumbRepository;

    public BreadCrumbService(BreadCrumbRepository breadCrumbRepository, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.breadCrumbRepository = breadCrumbRepository;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    private List<BreadCrumbItmDto> createItems(NameHierarchy nameHierarchy, String url, EntityType entityType) {
        List<BreadCrumbItmDto> items = new ArrayList<>();
        String[] tokens = url.split("/");
        String last = tokens[tokens.length - 1];
        boolean isNumeric = last.matches("\\d+");

        String currPath = "/organisation";
        if (nameHierarchy == null) {
            items.add(new BreadCrumbItmDto(false, "/organisation", "organisation"));
            items.add(new BreadCrumbItmDto(false, "/organisation/" + last, last));
        }
        if (nameHierarchy != null) {
            if (nameHierarchy.organisationName() != null) {
                items.add(new BreadCrumbItmDto(false, "/organisation", "Organisations"));
                if (entityType == EntityType.ORGANISATION) {
                    if (!isNumeric) {
                        items.add(new BreadCrumbItmDto(false, currPath += "/" + tokens[2], nameHierarchy.organisationName()));
                        items.add(new BreadCrumbItmDto(true, currPath += "/" + tokens[2], last));
                    } else {
                        items.add(new BreadCrumbItmDto(true, currPath += "/" + tokens[2], nameHierarchy.organisationName()));
                    }
                } else {
                    items.add(new BreadCrumbItmDto(false, currPath += "/" + tokens[2], nameHierarchy.organisationName()));
                }
            }
            if (entityType.ordinal() <= EntityType.DEPARTMENT.ordinal()) {
                if (entityType == EntityType.DEPARTMENT) {
                    if (!isNumeric) {
                        items.add(new BreadCrumbItmDto(false, currPath += "/department/" + tokens[4], nameHierarchy.departmentName()));
                        items.add(new BreadCrumbItmDto(true, currPath += "/department/" + tokens[4], last));
                    } else {
                        items.add(new BreadCrumbItmDto(true, currPath += "/department/" + tokens[4], last.matches("\\d+") ? nameHierarchy.departmentName() : last));
                    }
                } else {
                    items.add(new BreadCrumbItmDto(false, currPath += "/department/" + tokens[4], nameHierarchy.departmentName()));
                }
            }
            if (entityType.ordinal() <= EntityType.TEAM.ordinal()) {
                if (entityType == EntityType.TEAM) {
                    if (!isNumeric) {
                        items.add(new BreadCrumbItmDto(false, currPath += "/team/" + tokens[6], nameHierarchy.teamName()));
                        items.add(new BreadCrumbItmDto(true, currPath += "/team/" + tokens[6], last));
                    } else {
                        items.add(new BreadCrumbItmDto(true, currPath += "/team/" + tokens[6], last.matches("\\d+") ? nameHierarchy.teamName() : last));
                    }
                } else {
                    items.add(new BreadCrumbItmDto(false, currPath += "/team/" + tokens[6], nameHierarchy.teamName()));
                }
            }
            if (entityType.ordinal() <= EntityType.PROJECT.ordinal()) {
                if (entityType == EntityType.PROJECT) {
                    if (nameHierarchy.parentProjectName() != null) {
                        items.add(new BreadCrumbItmDto(false, currPath + "/project/" + nameHierarchy.parentProjectId(), nameHierarchy.parentProjectName()));
                        if (!isNumeric) {
                            items.add(new BreadCrumbItmDto(false, currPath += "/project/" + tokens[8], nameHierarchy.projectName()));
                            items.add(new BreadCrumbItmDto(true, currPath += "/project/" + tokens[8], last.matches("\\d+") ? nameHierarchy.projectName() : last));
                        } else {
                            items.add(new BreadCrumbItmDto(true, currPath += "/project/" + tokens[8], last.matches("\\d+") ? nameHierarchy.projectName() : last));
                        }
                    } else {
                        if (!isNumeric) {
                            items.add(new BreadCrumbItmDto(false, currPath += "/project/" + tokens[8], nameHierarchy.projectName()));
                            items.add(new BreadCrumbItmDto(true, currPath += "/project/" + tokens[8], last));
                        } else {
                            items.add(new BreadCrumbItmDto(true, currPath += "/project/" + tokens[8], last.matches("\\d+") ? nameHierarchy.projectName() : last));
                        }
                    }
                } else {
                    if (nameHierarchy.parentProjectName() != null) {
                        items.add(new BreadCrumbItmDto(false, currPath + "/project/" + nameHierarchy.parentProjectId(), nameHierarchy.parentProjectName()));
                        items.add(new BreadCrumbItmDto(false, currPath += "/project/" + tokens[8], nameHierarchy.projectName()));
                    } else {
                        items.add(new BreadCrumbItmDto(false, currPath += "/project/" + tokens[8], nameHierarchy.projectName()));
                    }
                }
            }
            if (entityType.ordinal() == EntityType.TASK.ordinal()) {
                if (entityType == EntityType.TASK) {
                    if (nameHierarchy.parentTaskName() != null) {
                        items.add(new BreadCrumbItmDto(false, currPath + "/task/" + nameHierarchy.parentTaskId(), nameHierarchy.parentTaskName()));
                        if (!isNumeric) {
                            items.add(new BreadCrumbItmDto(false, currPath + "/task/" + tokens[10], nameHierarchy.taskName()));
                            items.add(new BreadCrumbItmDto(true, currPath += "/task/" + tokens[10], last.matches("\\d+") ? nameHierarchy.taskName() : last));
                        } else {
                            items.add(new BreadCrumbItmDto(true, currPath += "/task/" + tokens[10], last.matches("\\d+") ? nameHierarchy.taskName() : last));
                        }
                    } else {
                        if (!isNumeric) {
                            items.add(new BreadCrumbItmDto(false, currPath += "/task/" + tokens[10], nameHierarchy.taskName()));
                            items.add(new BreadCrumbItmDto(true, currPath += "/task/" + tokens[10], last));
                        } else {
                            items.add(new BreadCrumbItmDto(true, currPath += "/task/" + tokens[10], last.matches("\\d+") ? nameHierarchy.taskName() : last));
                        }
                    }
                } else {
                    items.add(new BreadCrumbItmDto(true, currPath += "/task/" + tokens[10], nameHierarchy.taskName()));
                }
            }
        }

        return items;
    }

    private BreadCrumbDto createBreadCrumb(String url) {
        String strippedUrl = url.substring(1);
        String[] tokens = strippedUrl.split("/");
        boolean isNumeric = tokens[tokens.length - 1].matches("\\d+");
        boolean isSecondNumeric = tokens[tokens.length - 2].matches("\\d+");
        EntityType entityType;
        NameHierarchy nameHierarchy;
        if (!url.contains("department") && isNumeric) { //organisation
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[1]), EntityType.ORGANISATION);
            entityType = EntityType.ORGANISATION;
        } else if (!url.contains("department") && !isNumeric) { //organisation
            entityType = EntityType.ORGANISATION;
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[1]), EntityType.ORGANISATION);
        } else if (!url.contains("team") && isNumeric) { //department
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[3]), EntityType.DEPARTMENT);
            entityType = EntityType.DEPARTMENT;
        } else if (!url.contains("team") && !isNumeric) { //deptartment
            if (!isSecondNumeric) {
                entityType = EntityType.ORGANISATION;
            } else {
                entityType = EntityType.DEPARTMENT;
            }
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[1]), EntityType.ORGANISATION);

        } else if (!url.contains("project") && isNumeric) { //team
            entityType = EntityType.TEAM;
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[5]), EntityType.TEAM);
        } else if (!url.contains("project") && !isNumeric) { // team
            if (!isSecondNumeric) {
                entityType = EntityType.DEPARTMENT;
            } else {
                entityType = EntityType.TEAM;
            }
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[3]), EntityType.DEPARTMENT);
        } else if (!url.contains("task") && isNumeric) {  //project
            entityType = EntityType.PROJECT;
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[7]), EntityType.PROJECT);
        } else if (!url.contains("task") && !isNumeric) {  // project
            entityType = EntityType.PROJECT;
            if (!isSecondNumeric && !url.contains("subproject")) {
                entityType = EntityType.TEAM;
                nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[5]), EntityType.TEAM);
            } else {
                nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[7]), EntityType.PROJECT);
            }
        } else if (url.contains("task") && isNumeric) { // task
            entityType = EntityType.TASK;
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[9]), EntityType.TASK);
        } else if (url.contains("task") && !isNumeric) { // task
            entityType = EntityType.TASK;
            if (!isSecondNumeric && !url.contains("subtask")) {
                entityType = EntityType.PROJECT;
                nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[7]), EntityType.PROJECT);
            } else {
                nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[9]), EntityType.TASK);
            }
        } else {
            return new BreadCrumbDto(false, null);
        }
        List<BreadCrumbItmDto> items = createItems(nameHierarchy, url, entityType);
        return new BreadCrumbDto(true, items);
    }


    public BreadCrumbDto getBreadCrumb(HttpServletRequest request) {
        try {
            String url = request.getRequestURI();
            if (url.startsWith("/organisation/") && !url.startsWith("/organisation/create")) {
                return createBreadCrumb(url);
            }
            return new BreadCrumbDto(false, null);
        } catch (Exception e) {
            throw new EntityException("Noget gik galt med navigationen.", Alert.WARNING);
        }
    }
}
