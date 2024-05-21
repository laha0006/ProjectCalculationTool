package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.ProjectStatsDto;
import dev.tolana.projectcalculationtool.dto.ResourceEntityViewDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.service.ProjectService;
import dev.tolana.projectcalculationtool.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    void viewProject() throws Exception {
        when(projectService.getProject(1))
                .thenReturn(new ResourceEntityViewDto("name", "description", 1, 1, 1, 1, LocalDateTime.now(), 1, 1, 1, Status.TODO));
        when(projectService.getProjectStats(1))
                .thenReturn(new ProjectStatsDto(1,1,1,1,1,1,1,1,new HashMap<>(),new HashMap<>()));
        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}", 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("project/projectView"));
    }

    @Test
    @WithMockUser
    void showPageForCreatingProject() throws Exception {
        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/create", 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("project/createProject"));
    }

    @Test
    @WithMockUser
    void createProject() throws Exception {
        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/create", 1,1,1)
                .with(csrf())
                        .param("projectName", "name")
                        .param("description", "description")
                        .param("parentId", "1")
                        .param("teamId", "1")
                        .param("deadline", "2024-05-01T10:32")
                        .param("allottedHours", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1"));
    }

    @Test
    @WithMockUser
    void createSubProject() throws Exception {
        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/create", 1,1,1)
                        .with(csrf())
                        .param("projectName", "name")
                        .param("description", "description")
                        .param("parentId", "0")
                        .param("teamId", "1")
                        .param("deadline", "2024-05-01T10:32")
                        .param("allottedHours", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1"));
    }

    @Test
    @WithMockUser
    void getAllMembersFromTeamId() throws Exception {
        when(projectService.getProject(1))
                .thenReturn(new ResourceEntityViewDto("name", "description", 1, 1, 1, 1, LocalDateTime.now(), 1, 1, 1, Status.TODO));
        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/assign/members",1,1,1,1))
                .andExpect(status().isOk())
                .andExpect(view().name("project/viewAllTeamMembers"));
    }

//    @Test
//    @WithMockUser
//    void assignTeamMembersToProject() throws Exception {
//        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/assign/members",1,1,1,1)
//                        .with(csrf())
//                        .param("selectedTeamMembers", "member1", "member2", "member3")
//                        .param("role", "ROLE_ADMIN"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/organisation/1/department/1/team/1"));
//    }

    @Test
    @WithMockUser
    void deleteProject() throws Exception {
        ResourceEntityViewDto projectToDelete = new ResourceEntityViewDto("name", "description", 1, 0, 1, 1, LocalDateTime.now(), 1, 1, 1, Status.IN_PROGRESS);
        when(projectService.getProject(1))
                .thenReturn(projectToDelete);
        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/delete",1,1,1,1)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1"));
    }

    @Test
    @WithMockUser
    void deleteSubProject() throws Exception {
        ResourceEntityViewDto projectToDelete = new ResourceEntityViewDto("name", "description", 1, 1, 1, 1, LocalDateTime.now(), 1, 1, 1, Status.IN_PROGRESS);
        when(projectService.getProject(1))
                .thenReturn(projectToDelete);
        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/delete",1,1,1,1)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1"));
    }

    @Test
    @WithMockUser
    void displayEditProjectPage() throws Exception {
        ResourceEntityViewDto projectToEdit = new ResourceEntityViewDto("name", "description", 1, 1,1,1,LocalDateTime.now(), 1, 1,1,Status.TODO);
        when(projectService.getProject(1))
                .thenReturn(projectToEdit);

        List<Status> statusList = new ArrayList<>();
        statusList.add(Status.DONE);
        when(projectService.getStatusList())
                .thenReturn(statusList);

        mockMvc.perform(get("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/edit", 1, 1, 1, 1))
                .andExpect(status().isOk())
                .andExpect(view().name("project/editProject"));
    }

    @Test
    @WithMockUser
    void editProject() throws Exception {
//        ResourceEntityViewDto projectToEdit = new ResourceEntityViewDto("name", "description", 1, 1,1,1,LocalDateTime.now(), 1, 1,1,Status.TODO);
//        when(projectService.getProject(1))
//                .thenReturn(projectToEdit);

        mockMvc.perform(post("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/edit", 1, 1, 1, 1)
                        .with(csrf())
                        .param("resourceEntityName", "name")
                        .param("description", "description")
                        .param("id", "1")
                        .param("parentId", "1")
                        .param("teamId", "1")
                        .param("projectId", "1")
                        .param("deadline", "2024-12-12T10:32")
                        .param("estimatedHours", "1")
                        .param("actualHours", "1")
                        .param("allottedHours", "1")
                        .param("status", "TODO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/1/department/1/team/1/project/1"));
    }
}