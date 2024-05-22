package dev.tolana.projectcalculationtool.model;

import dev.tolana.projectcalculationtool.enums.Status;

import java.time.LocalDateTime;

public class Task extends ResourceEntity {

    private long projectId;
    private int estimatedHours;
    private int actualHours;

    public Task(long id,
                String name,
                String description,
                LocalDateTime dateCreated,
                boolean archived,
                LocalDateTime deadline,
                Status status,
                long parentId,
                long projectId,
                int estimatedHours,
                int actualHours) {
        super(id, name, description, dateCreated, archived, deadline, status, parentId);
        this.projectId = projectId;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
    }

    public Task(
            long id,
            String name,
            String description,
            LocalDateTime deadline,
            Status status,
            long parentId,
            long projectId,
            int estimatedHours) {
        super(id, name, description, deadline, status, parentId);
        this.projectId = projectId;
        this.estimatedHours = estimatedHours;
    }

    //used for mapping TaskEditDto To Task
    public Task(long id, String taskName, String description, long parentId, LocalDateTime deadline, int estimatedHours, int actualHours, Status status) {
        super(id, taskName, description, deadline, parentId, status);
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;

    }

    public Task(String taskName, String description, long projectId, long parentId, LocalDateTime deadline, int estimatedHours) {
        super(taskName, description, parentId, deadline);
        this.projectId = projectId;
        this.estimatedHours = estimatedHours;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public int getActualHours() {
        return actualHours;
    }

    public void setActualHours(int actualHours) {
        this.actualHours = actualHours;
    }
}
