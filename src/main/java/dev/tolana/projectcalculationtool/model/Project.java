package dev.tolana.projectcalculationtool.model;

import java.sql.Timestamp;

public class Project {
    private String name, description;
    private long teamId, parentId, projectId;
    private int allottedHours, status; //TODO make into Enum
    private Timestamp deadline;

    public Project(){}

    public Project(String name,
                   String description,
                   long teamId,
                   Timestamp deadline,
                   int allottedHours,
                   int status,
                   long parentId){
        this.name = name;
        this.description = description;
        this.teamId = teamId;
        this.deadline = deadline;
        this.allottedHours = allottedHours;
        this.status = status;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getAllottedHours() {
        return allottedHours;
    }

    public void setAllottedHours(int allottedHours) {
        this.allottedHours = allottedHours;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
