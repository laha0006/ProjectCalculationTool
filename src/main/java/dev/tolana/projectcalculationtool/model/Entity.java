package dev.tolana.projectcalculationtool.model;

import java.time.LocalDateTime;

public class Entity {

    private final long id;
    private final String name;
    private final String description;
    private final LocalDateTime dateCreated;
    private final boolean archived;

    public Entity(long id, String name, String description, LocalDateTime dateCreated, boolean archived) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateCreated = dateCreated;
        this.archived = archived;
    }

    //Task uses this constructor
    public Entity(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateCreated = LocalDateTime.now();
        this.archived = false;
    }

    //Used for mapping TaskCreationDto to Task
    public Entity(String taskName, String description) {
        this.id = 0;
        this.name = taskName;
        this.description =description;
        this.dateCreated = LocalDateTime.now();
        this.archived = false;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public boolean isArchived() {
        return archived;
    }
}
