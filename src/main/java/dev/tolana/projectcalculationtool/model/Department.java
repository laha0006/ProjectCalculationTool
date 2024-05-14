package dev.tolana.projectcalculationtool.model;

import java.time.LocalDateTime;

public class Department extends HierarchicalEntity{

    private final long organisationId;

    public Department(long id,
                      String name,
                      String description,
                      LocalDateTime dateCreated,
                      boolean archived,
                      long organisationId) {

        super(id, name, description, dateCreated, archived);
        this.organisationId = organisationId;
    }

    public long getOrganisationId() {
        return organisationId;
    }
}
