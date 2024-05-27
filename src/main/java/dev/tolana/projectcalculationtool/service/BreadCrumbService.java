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
        System.out.println("NAME HIERARCHY: " + nameHierarchy);
        System.out.println("CREATE ENTITY TYPE: " + entityType);
        String[] tokens = url.split("/");
        String last = tokens[tokens.length - 1];
        String secondLast = tokens[tokens.length - 2];
        boolean isNumeric = last.matches("\\d+");
        boolean isSecondNumeric = last.matches("\\d+");

        String currPath = "/organisation";
        if (nameHierarchy == null) {
            System.out.println("##### ?????  NUL ????? #######");
            items.add(new BreadCrumbItmDto(false, "/organisation", "organisation"));
            items.add(new BreadCrumbItmDto(false, "/organisation/" + last, last));
        }
        if (nameHierarchy != null) {
            System.out.println("<= ORGANISATION");
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
                System.out.println("<= DEPARTMENT");
                if (entityType == EntityType.DEPARTMENT) {
                    if (!isNumeric) {
                        System.out.println("DEPT isNUM FIRST");
                        items.add(new BreadCrumbItmDto(false, currPath += "/department/" + tokens[4], nameHierarchy.departmentName()));
                        items.add(new BreadCrumbItmDto(true, currPath += "/department/" + tokens[4], last));
                    } else {
                        System.out.println("DEPPT ELSE LAST");
                        items.add(new BreadCrumbItmDto(true, currPath += "/department/" + tokens[4], last.matches("\\d+") ? nameHierarchy.departmentName() : last));
                    }
                } else {
                    items.add(new BreadCrumbItmDto(false, currPath += "/department/" + tokens[4], nameHierarchy.departmentName()));
                }
            }
            if (entityType.ordinal() <= EntityType.TEAM.ordinal()) {
                System.out.println("<= TEAM");
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
                System.out.println("<= PROJECT");
                if (entityType == EntityType.PROJECT) {
                    if (nameHierarchy.parentProjectName() != null) {
                        System.out.println("   <= SUB_PROJECT last");
                        items.add(new BreadCrumbItmDto(false, currPath + "/project/" + nameHierarchy.parentProjectId(), nameHierarchy.parentProjectName()));
                        if (!isNumeric) {
                            items.add(new BreadCrumbItmDto(false, currPath += "/project/" + tokens[8], nameHierarchy.projectName()));
                            items.add(new BreadCrumbItmDto(true, currPath += "/project/" + tokens[8], last.matches("\\d+") ? nameHierarchy.projectName() : last));
                        } else {
                            items.add(new BreadCrumbItmDto(true, currPath += "/project/" + tokens[8], last.matches("\\d+") ? nameHierarchy.projectName() : last));
                        }
                    } else {
                        System.out.println("   <= SKIP project");
                        if (!isNumeric) {
                            items.add(new BreadCrumbItmDto(false, currPath += "/project/" + tokens[8], nameHierarchy.projectName()));
                            items.add(new BreadCrumbItmDto(true, currPath += "/project/" + tokens[8], last));
                        } else {
                            items.add(new BreadCrumbItmDto(true, currPath += "/project/" + tokens[8], last.matches("\\d+") ? nameHierarchy.projectName() : last));
                        }
                    }
                } else {
                    if (nameHierarchy.parentProjectName() != null) {
                        System.out.println("   <= SUB_PROJECT not last");
                        items.add(new BreadCrumbItmDto(false, currPath + "/project/" + nameHierarchy.parentProjectId(), nameHierarchy.parentProjectName()));
                        items.add(new BreadCrumbItmDto(false, currPath += "/project/" + tokens[8], nameHierarchy.projectName()));
                    } else {
                        items.add(new BreadCrumbItmDto(false, currPath += "/project/" + tokens[8], nameHierarchy.projectName()));
                    }
                }
            }
            if (entityType.ordinal() == EntityType.TASK.ordinal()) {
                System.out.println("<= TASK");
                if (entityType == EntityType.TASK) {
                    if (nameHierarchy.parentTaskName() != null) {
                        System.out.println("   <= SUB_TASK");
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
        int length = tokens.length;
        System.out.println("LENGTH: " + length);
        boolean isNumeric = tokens[tokens.length - 1].matches("\\d+");
        boolean isSecondNumeric = tokens[tokens.length - 2].matches("\\d+");
        System.out.println("isNumeric: " + isNumeric);
        System.out.println("isSecondNumeric: " + isSecondNumeric);
        EntityType entityType;
        NameHierarchy nameHierarchy;
        System.out.println("############## URL: " + strippedUrl);
        if (!url.contains("department") && isNumeric) { //organisation
            System.out.println("NO DEPT | IS NUMERIC");
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[1]), EntityType.ORGANISATION);
            entityType = EntityType.ORGANISATION;
        } else if (!url.contains("department") && !isNumeric) { //organisation
            System.out.println("NO DEPT | NOT NUMERIC");
            System.out.println("YOO WE DEPARTING OR WHAT?");
            entityType = EntityType.ORGANISATION;
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[1]), EntityType.ORGANISATION);
        } else if (!url.contains("team") && isNumeric) { //department
            System.out.println("NO TEAM | IS NUMERIC");
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[3]), EntityType.DEPARTMENT);
            entityType = EntityType.DEPARTMENT;
        } else if (!url.contains("team") && !isNumeric) { //deptartment
            System.out.println("NO TEAM | NOT NUMERIC");
            if (!isSecondNumeric) {
                entityType = EntityType.ORGANISATION;
            } else {
                entityType = EntityType.DEPARTMENT;
            }
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[1]), EntityType.ORGANISATION);

        } else if (!url.contains("project") && isNumeric) { //team
            System.out.println("NO PROJECT | IS NUMERIC");
            entityType = EntityType.TEAM;
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[5]), EntityType.TEAM);
        } else if (!url.contains("project") && !isNumeric) { // team
            System.out.println("NO PROJECT | NOT NUMERIC");
            if (!isSecondNumeric) {
                entityType = EntityType.DEPARTMENT;
            } else {
                entityType = EntityType.TEAM;
            }
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[3]), EntityType.DEPARTMENT);
        } else if (!url.contains("task") && isNumeric) {  //project
            System.out.println("NO TASK | IS NUMERIC");
            entityType = EntityType.PROJECT;
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[7]), EntityType.PROJECT);
        } else if (!url.contains("task") && !isNumeric) {  // project
            System.out.println("NO TASK | NOT NUMERIC");
            entityType = EntityType.PROJECT;
            if (!isSecondNumeric && !url.contains("subproject")) {
                entityType = EntityType.TEAM;
                nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[5]), EntityType.TEAM);
            } else {
                nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[7]), EntityType.PROJECT);
            }
        } else if (url.contains("task") && isNumeric) { // task
            System.out.println("IS TASK | IS NUMERIC");
            entityType = EntityType.TASK;
            nameHierarchy = breadCrumbRepository.getNameHierarchy(Long.parseLong(tokens[9]), EntityType.TASK);
        } else if (url.contains("task") && !isNumeric) { // task
            System.out.println("IS TASK | NOT NUMERIC");
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
