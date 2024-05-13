package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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


    @Test
    @WithMockUser()
    void createForm() throws Exception {
        mockMvc.perform(get("/task/{projectId}/create", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/createTask"));
    }

    @Test
    @WithMockUser()
    void createTask() throws Exception {
        mockMvc.perform(post("/task/create").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/task/overview/1"));
    }

    @Test
    @WithMockUser()
    void getTaskOverview() throws Exception{
        mockMvc.perform(get("/task/overview/{projectId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/viewAllTasks"));
    }
}