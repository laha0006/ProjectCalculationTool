package dev.tolana.projectcalculationtool.service;

import org.springframework.stereotype.Service;

@Service
public class BreadcrumbService {
    private final String HIERARCHY_NAME_SQL = """
            SELECT tsk.name  AS task,
                   tsk2.name AS parent_task,
                   p.name    AS project,
                   p2.name   AS parent_project,
                   tm.name   AS team,
                   d.name    AS department,
                   o.name    AS organisation
            FROM hierarchy h
                 LEFT JOIN task tsk
                           ON h.task_id = tsk.id
                 LEFT JOIN task tsk2
                           ON h.task_parent_id = tsk2.id
                 LEFT JOIN project p
                           ON h.project_id = p.id
                 LEFT JOIN project p2
                           ON h.project_parent_id = p2.id
                 LEFT JOIN team tm
                           ON h.team_id = tm.id
                 LEFT JOIN department d
                           ON h.department_id = d.id
                 LEFT JOIN organisation o
                           ON h.organisation_id = o.id
            """
