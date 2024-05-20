package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.TaskDto;
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
                           @PathVariable long taskId, Model model) {
        TaskDto task = taskService.getTask(taskId);
        model.addAttribute("parentTask", task);

        List<TaskDto> taskDtoList = taskService.getChildren(taskId);
        model.addAttribute("allTasks", taskDtoList);

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);

        return "task/taskView";
    }

    @GetMapping("/create")
    public String sendCreateForm(@PathVariable long orgId,
                                 @PathVariable long deptId,
                                 @PathVariable long teamId,
                                 @PathVariable long projectId,
                                 Model model) {
        model.addAttribute("taskDto", new TaskDto("", "", projectId, LocalDateTime.now(), 0, Status.TODO, 0, -1));
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

        TaskDto parentTask = taskService.getTask(taskId);
        String parentTaskName = parentTask.taskName();
        model.addAttribute("parentTaskName", parentTaskName);
        model.addAttribute("taskDto", new TaskDto("", "", projectId, LocalDateTime.now(), 0, Status.TODO, taskId, -1));
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
                             @ModelAttribute TaskDto newTask,
                             Authentication authentication) {

        String username = authentication.getName();
        taskService.createTask(username, newTask);
        long parentTaskId = newTask.parentId();

        return determineRedirection(orgId, deptId, teamId, projectId, parentTaskId);
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable long orgId,
                             @PathVariable long deptId,
                             @PathVariable long teamId,
                             @PathVariable long projectId,
                             @PathVariable long taskId) {

        TaskDto taskToDelete = taskService.getTask(taskId);
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

        TaskDto taskToEdit = taskService.getTask(taskId);
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
    public String editTask(Model model,
                           @PathVariable long orgId,
                           @PathVariable long deptId,
                           @PathVariable long teamId,
                           @PathVariable long projectId,
                           @ModelAttribute TaskDto taskToEdit) {

        long parentTaskId = taskToEdit.parentId();

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("projectId", projectId);

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
