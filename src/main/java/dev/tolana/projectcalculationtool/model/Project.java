package dev.tolana.projectcalculationtool.model;

import dev.tolana.projectcalculationtool.enums.Status;

import java.time.LocalDateTime;

public class Project extends ResourceEntity{

    private long teamId;
    private int allottedHours;

    public Project(long id,
                   String name,
                   String description,
                   LocalDateTime dateCreated,
                   boolean archived,
                   LocalDateTime deadline,
                   Status status,
                   long parentId,
                   long teamId,
                   int allottedHours) {
        super(id, name, description, dateCreated, archived, deadline, status, parentId);
        this.teamId = teamId;
        this.allottedHours = allottedHours;
    }

    //constructor for mapping ProjectCreationDto to Entity
    public Project(String projectName, String description, long parentId, long teamId, LocalDateTime deadline, int allottedHours) {
        super(projectName, description, parentId, deadline);
        this.teamId = teamId;
        this.allottedHours = allottedHours;
    }

    //used for mapping ProjectEditDto to Entity
    public Project(long id, String projectName, String description, LocalDateTime deadline, int allottedHours, Status status) {
        super(id, projectName, description, deadline, status);
        this.allottedHours = allottedHours;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getAllottedHours() {
        return allottedHours;
    }
}
