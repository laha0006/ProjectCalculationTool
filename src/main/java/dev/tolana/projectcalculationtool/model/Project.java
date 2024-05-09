package dev.tolana.projectcalculationtool.model;

import java.sql.Time;
import java.sql.Timestamp;

public class Project {
    private String name, description;
    private long teamId, parentId, projectId;
    private int allottedHours, status; //TODO make into Enum
    private Timestamp deadline, dateCreated;
    private boolean isArchived;

    public Project(){}

    public Project(long projectId,
                   String name,
                   String description,
                   long teamId,
                   Timestamp dateCreated,
                   Timestamp deadline,
                   int allottedHours,
                   int status,
                   long parentId,
                   boolean isArchived){
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.teamId = teamId;
        this.dateCreated = dateCreated;
        this.deadline = deadline;
        this.allottedHours = allottedHours;
        this.status = status;
        this.parentId = parentId;
        this.isArchived = isArchived;
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

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
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

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
