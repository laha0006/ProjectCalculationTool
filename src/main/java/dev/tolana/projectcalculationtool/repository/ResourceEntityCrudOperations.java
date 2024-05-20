package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.enums.Status;

import java.util.List;

public interface ResourceEntityCrudOperations extends EntityCrudOperations {

    boolean changeStatus(long resourceEntityId);

    List<Status> getStatusList();
}
