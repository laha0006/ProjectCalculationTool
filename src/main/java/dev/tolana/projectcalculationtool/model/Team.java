package dev.tolana.projectcalculationtool.model;

import java.time.LocalDateTime;


public class Team {

    private long id;
    private String name;
    private String description;
    private long department_id;
    private LocalDateTime dateCreated;
    private boolean archived;


    public Team(long id, String name, String description, long department_id, LocalDateTime dateCreated, boolean archived) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.department_id = department_id;
        this.dateCreated = dateCreated;
        this.archived = archived;
    }

    public long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(long department_id) {
        this.department_id = department_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
