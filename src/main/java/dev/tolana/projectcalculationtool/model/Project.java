package dev.tolana.projectcalculationtool.model;

import java.sql.Timestamp;

public class Project {
    private String name, description;
    private int team_id, parent_id, allotted_hours;
    private int status; //TODO make into Enum
    private Timestamp deadline;

    public Project(String name, String description, int team_id, Timestamp deadline,
                   int allotted_hours, int status, int parent_id){
        this.name = name;
        this.description = description;
        this.team_id = team_id;
        this.deadline = deadline;
        this.allotted_hours = allotted_hours;
        this.status = status;
        this.parent_id = parent_id;
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

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getAllotted_hours() {
        return allotted_hours;
    }

    public void setAllotted_hours(int allotted_hours) {
        this.allotted_hours = allotted_hours;
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
}
