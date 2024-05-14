package dev.tolana.projectcalculationtool.model;

import java.time.LocalDateTime;

public class Team extends HierarchicalEntity{

    private final long departmentId;

    public Team(long id,
                String name,
                String description,
                LocalDateTime dateCreated,
                boolean archived,
                long departmentId) {

        super(id, name, description, dateCreated, archived);
        this.departmentId = departmentId;
    }

    public long getDepartmentId() {
        return departmentId;
    }
}
