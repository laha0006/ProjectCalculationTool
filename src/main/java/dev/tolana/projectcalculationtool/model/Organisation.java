package dev.tolana.projectcalculationtool.model;

import java.time.LocalDateTime;


public class Organisation extends Entity {
    public Organisation(long id, String name, String description, LocalDateTime dateCreated, boolean archived) {
        super(id, name, description, dateCreated, archived);
    }
}
