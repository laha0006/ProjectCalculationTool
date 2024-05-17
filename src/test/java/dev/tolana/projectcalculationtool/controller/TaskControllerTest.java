package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.service.TaskService;
import dev.tolana.projectcalculationtool.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;

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
    void getTaskOverview() throws Exception {
        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/overview", 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/viewAllTasks"));
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
        TaskDto taskDto = new TaskDto("name", "description", 1, LocalDateTime.now(), 1, Status.TODO, 1, 1);

        when(taskService.getTaskOnId(1))
                .thenReturn(taskDto);
        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}/create/subtask", 1, 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/createTask"));
    }

//    @Test
//    @WithMockUser()
//    void createTask() throws Exception {
//        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/create", 1, 1, 1, 1)
//                        .with(csrf())
//                        .param("taskName", "1")
//                        .param("taskDescription", "1")
//                        .param("projectId", "1")
//                        .param("deadline", "2024-05-01T10:32")
//                        .param("estimatedHours", "1")
//                        .param("status", "1")
//                        .param("parentId", "1")
//                        .param("taskId", "1")
//                        .param("teamId", "1")
//                        .param("deptId", "1")
//                        .param("orgId", "1")
//                )
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1/task/overview"));
//    }

    @Test
    @WithMockUser
    void deleteTask() throws Exception {
        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task/{taskId}/delete", 1, 1, 1, 1, 1)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1/task/overview"));
    }
}