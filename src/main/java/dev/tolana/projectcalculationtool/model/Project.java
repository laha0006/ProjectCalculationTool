package dev.tolana.projectcalculationtool.model;

import dev.tolana.projectcalculationtool.enums.Status;

import java.sql.Time;
import java.sql.Timestamp;
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
