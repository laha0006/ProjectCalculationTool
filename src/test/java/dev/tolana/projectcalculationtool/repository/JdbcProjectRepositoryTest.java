package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.enums.Alert;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.exception.EntityException;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.impl.JdbcProjectRepository;
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
class JdbcProjectRepositoryTest {

    @Autowired
    private JdbcProjectRepository jdbcProjectRepository;

    @Test
    void attempt_Create_Project_With_Name_Longer_Than_50_Character_And_Description_Longer_Than_100_Characters() {
        String username = "vz";
        Entity projectWithName51CharacterAndDescription100Characters = new Project(
                0,
                "mainTaskaamainTaskaamainTaskaamainTaskaamainTaskaa1",
                "mainTaskaamainTaskaamainTaskaamainTaskaamainTaskaa1mainTaskaamainTaskaamainTaskaamainTaskaamainTaskaa",
                LocalDateTime.now(),
                false,
                LocalDateTime.now(),
                Status.TODO,
                0,
                1,
                1);

        assertThrows(EntityException.class, ()-> jdbcProjectRepository.createEntity(username, projectWithName51CharacterAndDescription100Characters));
    }

    @Test
    void attempt_Create_Project_With_No_Name_And_Description() {
        String username = "vz";
        Entity projectWithNoNameOrDescription = new Project(
                0,
                null,
                null,
                LocalDateTime.now(),
                false,
                LocalDateTime.now(),
                Status.TODO,
                0,
                1,
                1);

        assertThrows(EntityException.class, ()-> jdbcProjectRepository.createEntity(username, projectWithNoNameOrDescription));
    }

    @Test
    void createProject() {
       Entity projectToCreate = new Project(0,"mainProject","mainDescription",LocalDateTime.now(),false,LocalDateTime.now(),Status.TODO,0,1, 1);
        String username = "vz";

        jdbcProjectRepository.createEntity(username, projectToCreate);
        Entity project = jdbcProjectRepository.getEntityOnId(10);

        String expectedName = "mainProject";
        String actualName = project.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    void createSubProject() {
        Entity subProjectToCreate = new Project(0,"subProject","subProjectDescription",LocalDateTime.now(),false,LocalDateTime.now(),Status.TODO,1,1, 1);
        String username = "vz";

        jdbcProjectRepository.createEntity(username, subProjectToCreate);
        Entity subProject = jdbcProjectRepository.getEntityOnId(10);

        String expectedName = "subProject";
        String actualName = subProject.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    void getProjectOnId() {
        Entity project = jdbcProjectRepository.getEntityOnId(1);
        String expectedTaskName = project.getName();

        assertEquals("Frontend Project", expectedTaskName);
    }

    @Test
    void getSubProjectsFromProject() {
        List<Project> subProjectList = jdbcProjectRepository.getSubProjects(1);

        String expectedNameForSubProject = "sub one";
        String actualNameForSubProject = subProjectList.get(0).getName();

        assertEquals(expectedNameForSubProject, actualNameForSubProject);
    }

    @Test
    void get_Only_Task_And_Not_Sub_Tasks_From_Project() {
        List<Entity> taskList = jdbcProjectRepository.getChildren(1);

        List<String> actualTaskNames = new ArrayList<>();
        for (Entity task : taskList) {
            actualTaskNames.add(task.getName());
        }

        List<String> expectedTaskNames = new ArrayList<>(List.of(
                "Frontend Task: 1",
                "Frontend Task: 2",
                "Frontend Task: 3",
                "Frontend Task: 4",
                "Frontend Task: 5",
                "Frontend Task: 6",
                "Frontend Task: 7",
                "MainTask Task"));
        assertEquals(expectedTaskNames, actualTaskNames);

        int expectedLength = 8;
        int actualLength = taskList.size();
        assertEquals(expectedLength, actualLength);

    }

    @Test
    void editEntity() {
        Entity projectToEdit = new Project(1,"editedSubProject","subProjectDescription",LocalDateTime.now(),false,LocalDateTime.now(),Status.TODO,0,1, 1);
        jdbcProjectRepository.editEntity(projectToEdit);

        Entity editedProject = jdbcProjectRepository.getEntityOnId(1);
        String expectedName = "editedSubProject";
        String actualName = editedProject.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    void deleteEntity() {
        jdbcProjectRepository.deleteEntity(1);
        assertThrows(EntityException.class, () -> jdbcProjectRepository.getEntityOnId(1));
    }

    @Test
    void getStatusList() {
        List<Status> expectedList = new ArrayList<>(List.of(
                Status.IN_PROGRESS,
                Status.DONE,
                Status.IN_REVIEW,
                Status.TODO));

        List<Status> actualList = jdbcProjectRepository.getStatusList();

        assertEquals(expectedList, actualList);
    }
}