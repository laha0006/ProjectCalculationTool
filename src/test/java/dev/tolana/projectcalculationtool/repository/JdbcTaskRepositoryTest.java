package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.repository.impl.JdbcTaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;


@SpringBootTest
class JdbcTaskRepositoryTest {

    @Autowired
    private JdbcTaskRepository jdbcTaskRepository;
    @AfterEach
    void databaseCleanUp() throws SQLException {
    }
    @Test
    void createEntity() {
    }

    @Test
    void getEntityOnId() {
    }

    @Test
    void getAllEntitiesOnUsername() {
    }

    @Test
    void getAllEntitiesOnId() {
    }

    @Test
    void editEntity() {
    }

    @Test
    void deleteEntity() {
    }

    @Test
    void inviteToEntity() {
    }

    @Test
    void archiveEntity() {
    }

    @Test
    void assignUser() {
    }

    @Test
    void getUsersFromEntityId() {
    }

    @Test
    void getAllUserRoles() {
    }

    @Test
    void changeStatus() {
    }
}