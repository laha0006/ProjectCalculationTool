package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.TaskCreationDto;
import dev.tolana.projectcalculationtool.dto.TaskEditDto;
import dev.tolana.projectcalculationtool.dto.TaskViewDto;
import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.repository.impl.JdbcTaskRepository;
import dev.tolana.projectcalculationtool.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task")
public class TaskController {

    private final TaskService taskService;
    private final JdbcTaskRepository jdbcTaskRepository;

    public TaskController(TaskService taskService, JdbcTaskRepository jdbcTaskRepository) {
        this.taskService = taskService;
        this.jdbcTaskRepository = jdbcTaskRepository;
    }

    @GetMapping("/{taskId}")
    public String viewTask(@PathVariable long orgId,
                           @PathVariable long deptId,
                           @PathVariable long teamId,
                           @PathVariable long projectId,
                           @PathVariable long taskId, Model model) {
        TaskViewDto task = taskService.getTaskToView(taskId);
        model.addAttribute("parentTask", task);

        List<TaskViewDto> taskList = taskService.getChildren(taskId);
        model.addAttribute("allTasks", taskList);

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("projectId", projectId);

        return "task/taskView";
    }

    @GetMapping("/create")
    public String sendCreateForm(@PathVariable long orgId,
                                 @PathVariable long deptId,
                                 @PathVariable long teamId,
                                 @PathVariable long projectId,
                                 Model model) {

        TaskCreationDto taskToCreate = new TaskCreationDto("", "", projectId, 0, LocalDateTime.now(), 0);
        model.addAttribute("taskToCreate", taskToCreate);

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("projectId", projectId);

        return "task/createTask";
    }

    @GetMapping("/{taskId}/create/subtask")
    public String sendCreateSubTaskForm(@PathVariable long orgId,
                                        @PathVariable long deptId,
                                        @PathVariable long teamId,
                                        @PathVariable long projectId,
                                        @PathVariable long taskId,
                                        Model model) {

        TaskViewDto parentTask = taskService.getTaskToView(taskId);
        String parentTaskName = parentTask.taskName();
        model.addAttribute("parentTaskName", parentTaskName);

        TaskCreationDto subTaskToCreate = new TaskCreationDto("", "", projectId, taskId, LocalDateTime.now(), 0);
        model.addAttribute("subTaskToCreate", subTaskToCreate);

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);

        return "task/createTask";
    }

    @PostMapping("/create")
    public String createTask(@PathVariable long orgId,
                             @PathVariable long deptId,
                             @PathVariable long teamId,
                             @PathVariable long projectId,
                             @ModelAttribute TaskCreationDto taskToCreate,
                             Authentication authentication) {

        String username = authentication.getName();
        taskService.createTask(username, taskToCreate);
        long parentTaskId = taskToCreate.parentId();

        return determineRedirection(orgId, deptId, teamId, projectId, parentTaskId);
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable long orgId,
                             @PathVariable long deptId,
                             @PathVariable long teamId,
                             @PathVariable long projectId,
                             @PathVariable long taskId) {

        TaskEditDto taskToDelete = taskService.getTaskToEdit(taskId);
        long parentTaskId = taskToDelete.parentId();

        taskService.deleteTask(taskId);

        return determineRedirection(orgId, deptId, teamId, projectId, parentTaskId);
    }

    @GetMapping("{taskId}/edit")
    public String displayEditTaskPage(@PathVariable long orgId,
                                      @PathVariable long deptId,
                                      @PathVariable long teamId,
                                      @PathVariable long projectId,
                                      @PathVariable long taskId,
                                      Model model) {

        TaskEditDto taskToEdit = taskService.getTaskToEdit(taskId);
        model.addAttribute("taskToEdit", taskToEdit);

        List<Status> statusList = taskService.getStatusList();
        model.addAttribute("statusList", statusList);

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("projectId", projectId);

        return "task/editTask";
    }

    @GetMapping("/{taskId}/members")
    public String displayMembersPage(@PathVariable("orgId") long orgId,
                                     @PathVariable("deptId") long deptId,
                                     @PathVariable("teamId") long teamId,
                                     @PathVariable("projectId") long projectId,
                                     @PathVariable("taskId") long taskId,
                                     Model model, Authentication authentication) {
        TaskViewDto task = taskService.getTaskToView(taskId);
        model.addAttribute("task", task);
        System.out.println("task = " + task);

        List<UserEntityRoleDto> users = taskService.getUsersFromTaskId(taskId);
        String username = authentication.getName();
        model.addAttribute("isMember", users.contains(new UserEntityRoleDto(username, -1, -1, -1, -1, -1, -1)));
        model.addAttribute("users", users);

        return "task/taskMembers";
    }

    @PostMapping("{taskId}/edit")
    public String editTask(@PathVariable long orgId,
                           @PathVariable long deptId,
                           @PathVariable long teamId,
                           @PathVariable long projectId,
                           @ModelAttribute TaskEditDto taskToEdit) {

        long parentTaskId = taskToEdit.parentId();
        taskService.editTask(taskToEdit);

        return determineRedirection(orgId, deptId, teamId, projectId, parentTaskId);
    }

    @PostMapping("/{taskId}/assign")
    public String assignMemberToTask(@PathVariable long orgId,
                                     @PathVariable long deptId,
                                     @PathVariable long teamId,
                                     @PathVariable long projectId,
                                     @PathVariable long taskId, Authentication authentication) {
        System.out.println("WE ARE HERE AT TASK ASSINGMENT OFFICES SMILEY FACE.");
        String username = authentication.getName();
        taskService.assignMemberToTask(username, taskId);
        return determineRedirection(orgId, deptId, teamId, projectId, taskId) + "/members";
    }


    @PostMapping("/{taskId}/members/promote/{username}")
    public String promoteMemberToAdmin(@PathVariable("orgId") long orgId,
                                       @PathVariable("deptId") long deptId,
                                       @PathVariable("teamId") long teamId,
                                       @PathVariable("projectId") long projectId,
                                       @PathVariable("taskId") long taskId,
                                       @PathVariable("username") String username) {



        taskService.promoteMemberToAdmin(projectId, username);


        return determineRedirection(orgId, deptId, teamId, projectId, taskId) + "/members";
    }

    @PostMapping("/{taskId}/members/kick/{username}")
    public String kickMemberFromTask(@PathVariable("orgId") long orgId,
                                           @PathVariable("deptId") long deptId,
                                           @PathVariable("teamId") long teamId,
                                           @PathVariable("projectId") long projectId,
                                           @PathVariable("taskId") long taskId,
                                           @PathVariable("username") String username) {


        taskService.kickMemberFromTask(taskId, username);

        return determineRedirection(orgId, deptId, teamId, projectId, taskId) + "/members";
    }


    private String determineRedirection(long orgId, long deptId, long teamId, long projectId, long parentTaskId) {
        if (parentTaskId == 0) { //if parentId == 0, it means it has no parent, therefor it's not a subtask.
            return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId;

        } else
            return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId + "/task/" + parentTaskId;
    }
}
