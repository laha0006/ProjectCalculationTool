package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.TaskCreationDto;
import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.dto.TaskEditDto;
import dev.tolana.projectcalculationtool.dto.TaskViewDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.service.TaskService;
import dev.tolana.projectcalculationtool.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser()
    void viewTask() throws Exception {
        TaskViewDto viewTask = new TaskViewDto("name", "description", 1, LocalDateTime.now(), 1, 1, Status.TODO);
        when(taskService.getTaskToView(1))
                .thenReturn(viewTask);

        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}", 1, 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/taskView"));
    }

    @Test
    @WithMockUser()
    void createForm() throws Exception {
        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/create", 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/createTask"));
    }

    @Test
    @WithMockUser
    void sendCreateSubTaskForm() throws Exception {
        TaskViewDto subTaskToCreate = new TaskViewDto("name", "description", 1, LocalDateTime.now(), 1, 1, Status.TODO);

        when(taskService.getTaskToView(1))
                .thenReturn(subTaskToCreate);
        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}/create/subtask", 1, 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/createTask"));
    }

    @Test
    @WithMockUser()
    void createTask() throws Exception {
        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/create", 1, 1, 1, 1)
                        .with(csrf())
                        .param("taskName", "1")
                        .param("taskDescription", "1")
                        .param("projectId", "1")
                        .param("deadline", "2024-05-01T10:32")
                        .param("estimatedHours", "1")
                        .param("status", "TODO")
                        .param("parentId", "1")
                        .param("taskId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1/task/1"));
    }

    @Test
    @WithMockUser
    void deleteTask() throws Exception {
        TaskEditDto taskToDelete = new TaskEditDto(1, "name","description", 0, LocalDateTime.now(), 1, 1, Status.TODO);
        when(taskService.getTaskToEdit(1))
                .thenReturn(taskToDelete);
        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}/delete", 1, 1, 1, 1, 1)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1"));
    }

    @Test
    @WithMockUser
    void deleteSubTask() throws Exception {
        TaskEditDto subTaskToDelete = new TaskEditDto(1, "name","description", 1, LocalDateTime.now(), 1, 1, Status.TODO);
        when(taskService.getTaskToEdit(1))
                .thenReturn(subTaskToDelete);
        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}/delete", 1, 1, 1, 1, 1)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1/task/1"));
    }

    @Test
    @WithMockUser
    void displayEditTaskPage() throws Exception {
        TaskEditDto taskToEdit = new TaskEditDto(1, "name","description", 0, LocalDateTime.now(), 1, 1, Status.TODO);
        when(taskService.getTaskToEdit(1))
                .thenReturn(taskToEdit);

        List<Status> statusList = new ArrayList<>();
        statusList.add(Status.DONE);
        when(taskService.getStatusList())
                .thenReturn(statusList);

        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}/edit", 1, 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/editTask"));
    }

    @Test
    @WithMockUser
    void editTask() throws Exception {
        TaskEditDto taskToEdit = new TaskEditDto(1, "name","description", 0, LocalDateTime.now(), 1, 1, Status.TODO);
        when(taskService.getTaskToEdit(1))
                .thenReturn(taskToEdit);

        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}/delete", 1, 1, 1, 1, 1)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1"));
    }

    @Test
    @WithMockUser
    void editSubTask() throws Exception {
        TaskEditDto taskToEdit = new TaskEditDto(1, "name","description", 1, LocalDateTime.now(), 1, 1, Status.TODO);
        when(taskService.getTaskToEdit(1))
                .thenReturn(taskToEdit);

        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}/delete", 1, 1, 1, 1, 1)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1/task/1"));
    }
}