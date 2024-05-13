package dev.tolana.projectcalculationtool.model;

import java.time.LocalDate;

public class Task {

    private long taskId;
    private String taskName;
    private String taskDescription;
    private long projectId;
    private LocalDate dateCreated;
    private LocalDate deadline;
    private int estimatedHours;
    private int status;
    private long parentId;
    private boolean isArchived;

    public Task(long taskId,
                String taskName,
                String taskDescription,
                long projectId,
                LocalDate dateCreated,
                LocalDate deadline,
                int estimatedHours,
                int status,
                long parentId,
                boolean isArchived) {

        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.projectId = projectId;
        this.dateCreated = dateCreated;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
        this.status = status;
        this.parentId = parentId;
        this.isArchived = isArchived;
    }

    public Task(String taskName,
                String taskDescription,
                long projectId,
                LocalDate deadline,
                int estimatedHours,
                int status,
                long parentId,
                long taskId) {

        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.projectId = projectId;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
        this.status = status;
        this.parentId = parentId;
        this.taskId = taskId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
