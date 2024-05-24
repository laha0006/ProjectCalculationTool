package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.impl.JdbcTaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:init_DB.sql")
class JdbcTaskRepositoryTest {

    @Autowired
    private JdbcTaskRepository jdbcTaskRepository;

    @Test
    void createTask() {
        Entity taskToCreate = new Task(0, "mainTask", "MainTask", LocalDateTime.now(), false, LocalDateTime.now(), Status.TODO, 0, 1, 1, 1);
        String username = "vz";

        jdbcTaskRepository.createEntity(username, taskToCreate);
        Entity task = jdbcTaskRepository.getEntityOnId(17);

        String expectedName = "mainTask";
        String actualName = task.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    void createSubTask() {
        Entity taskToCreate = new Task(0, "subtasktest", "subtasktest", LocalDateTime.now(), false, LocalDateTime.now(), Status.TODO, 1, 1, 1, 1);
        String username = "vz";

        jdbcTaskRepository.createEntity(username, taskToCreate);
        Entity task = jdbcTaskRepository.getEntityOnId(17);

        String expectedName = "subtasktest";
        String actualName = task.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    void getTaskOnId() {
        Entity task = jdbcTaskRepository.getEntityOnId(1);
        String expectedTaskName = task.getName();

        assertEquals("Frontend Task", expectedTaskName);
    }

    @Test
    void getSubTasksFromTask() {
        List<Entity> subtaskList = jdbcTaskRepository.getChildren(15);

        String expectedNameForSubtask = "SubTask Task";
        String actualNameForSubtask = subtaskList.get(0).getName();

        assertEquals(expectedNameForSubtask, actualNameForSubtask);
    }

    @Test
    void editEntity() {
        Entity taskToEdit = new Task(1, "editedTask", "test", LocalDateTime.now(), false, LocalDateTime.now(), Status.TODO, 0, 1, 1, 1);
        jdbcTaskRepository.editEntity(taskToEdit);

        Entity editedTask = jdbcTaskRepository.getEntityOnId(1);
        String expectedName = "editedTask";
        String actualName = editedTask.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    void deleteEntity() {
        jdbcTaskRepository.deleteEntity(1);
        assertThrows(EntityException.class, ()-> jdbcTaskRepository.getEntityOnId(1));
    }

    @Test
    void getStatusList() {
        List<Status> expectedList = new ArrayList<>(List.of(
                Status.IN_PROGRESS,
                Status.DONE,
                Status.IN_REVIEW,
                Status.TODO));

        List<Status> actualList = jdbcTaskRepository.getStatusList();

        assertEquals(expectedList, actualList);
    }
}