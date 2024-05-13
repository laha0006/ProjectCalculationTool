package dev.tolana.projectcalculationtool.repository;

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
    void createParentTask() {

    }

    @Test
    void deleteTask() {
    }

    @Test
    void editTask() {
    }

    @Test
    void getTask() {
    }

    @Test
    void getAllTasks() {
    }

    @Test
    void getAllProjectTasks() {
    }
}