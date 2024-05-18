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

//    @GetMapping("/overview")
//    public String getTaskOverview(@PathVariable long projectId,
//                                  Model model) {
//
//        List<TaskDto> taskList = taskService.getAllProjectTasks(projectId);
//        model.addAttribute("projectId", projectId);
//
//        model.addAttribute("taskList", taskList);
//        return "task/viewAllTasks";
//    }

    @GetMapping("/{taskId}")
    public String viewTask(@PathVariable long orgId,
                           @PathVariable long deptId,
                           @PathVariable long teamId,
                           @PathVariable long taskId, Model model) {
        TaskDto task = taskService.getTask(taskId);
        model.addAttribute("task", task);

        List<TaskDto> taskDtoList = taskService.getChildren(taskId);
        model.addAttribute("allTasks", taskDtoList);

        model.addAttribute("orgId", orgId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("teamId", teamId);

        return "task/taskView";
    }

//    @GetMapping("/{taskId}/view/subtask")
//    public String viewSubtask(@PathVariable long taskId, Model model) {
//        TaskDto parentTask = taskService.getTask(taskId);
//        model.addAttribute("parentTask", parentTask);
//
//        List<TaskDto> subtasks = taskService.getChildren(taskId);
//        model.addAttribute("allSubtasks", subtasks);
//
//        return "task/viewSubtask";
//    }

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

        String redirectUrl;
        if (newTask.parentId() == 0){ //if parentId == 0, it means it has no parent, therefor it's not a subtask.
            redirectUrl = "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId;
        } else
            redirectUrl = "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId + "/task/" + newTask.parentId();

        return redirectUrl;
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable long orgId,
                             @PathVariable long deptId,
                             @PathVariable long teamId,
                             @PathVariable long projectId,
                             @PathVariable long taskId) {

        taskService.deleteTask(taskId);

        return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId;
    }
}
