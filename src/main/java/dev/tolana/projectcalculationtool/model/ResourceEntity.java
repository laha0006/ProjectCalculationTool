package dev.tolana.projectcalculationtool.model;

import dev.tolana.projectcalculationtool.enums.Status;

import java.time.LocalDateTime;

public class ResourceEntity extends Entity{

    private LocalDateTime deadline;
    private Status status;
    private long parentId;
    public ResourceEntity(long id,
                          String name,
                          String description,
                          LocalDateTime dateCreated,
                          boolean archived,
                          LocalDateTime deadline,
                          Status status,
                          long parentId
    ) {
        super(id, name, description, dateCreated, archived);
        this.deadline = deadline;
        this.status = status;
        this.parentId = parentId;
    }

    public ResourceEntity(long id,
                          String name,
                          String description,
                          LocalDateTime deadline,
                          Status status,
                          long parentId) {

        super(id, name, description);
        this.deadline = deadline;
        this.status = status;
        this.parentId = parentId;
    }

    //Constructor used for mapping TaskEditDto to Entity
    public ResourceEntity(long id, String taskName, String description, LocalDateTime deadline, long parentId, Status status) {
        super(id, taskName, description);
        this.parentId = parentId;
        this.status = status;
        this.deadline = deadline;
    }

    //Constructor used for mapping of TaskCreationDto to Entity
    public ResourceEntity(String taskName, String description, long parentId, LocalDateTime deadline) {
        super(taskName, description);
        this.parentId = parentId;
        this.deadline = deadline;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getStatusId() {
        return status.getId();
    }
}
