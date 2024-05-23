package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.TaskCreationDto;
import dev.tolana.projectcalculationtool.dto.TaskEditDto;
import dev.tolana.projectcalculationtool.dto.TaskViewDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/organisation/{orgId}/department/{deptId}/team/{teamId}/project/{projectId}/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
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

    private String determineRedirection(long orgId, long deptId, long teamId, long projectId, long parentTaskId) {
        if (parentTaskId == 0){ //if parentId == 0, it means it has no parent, therefor it's not a subtask.
           return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId;

        } else
            return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId + "/task/" + parentTaskId;
    }
}
