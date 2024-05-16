package dev.tolana.projectcalculationtool.repository;

public interface ResourceEntityCrudOperations extends EntityCrudOperations {

    boolean changeStatus(long resourceEntityId);
}
