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

    @GetMapping("/overview")
    public String getTaskOverview(@PathVariable long projectId,
                                  Model model) {

        List<TaskDto> taskList = taskService.getAllProjectTasks(projectId);
        model.addAttribute("projectId", projectId);

        model.addAttribute("taskList", taskList);
        return "task/viewAllTasks";
    }

    @GetMapping("/create")
    public String sendCreateForm(@PathVariable long projectId,
                                 Model model) {
        model.addAttribute("taskDto", new TaskDto("", "", projectId, LocalDateTime.now(), 0, Status.TODO, 0, -1));
        model.addAttribute("projectId", projectId);

        return "task/createTask";
    }

    @GetMapping("/{taskId}/create/subtask")
    public String sendCreateSubTaskForm(@PathVariable long projectId,
                                        @PathVariable long taskId,
                                        Model model) {

        TaskDto parentTask = taskService.getTaskOnId(taskId);
        String parentTaskName = parentTask.taskName();
        model.addAttribute("parentTaskName", parentTaskName);
        model.addAttribute("taskDto", new TaskDto("", "", projectId, LocalDateTime.now(), 0, Status.TODO, taskId, -1));
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

        return "redirect:/organisation/" + orgId + "/department/" + deptId + "/team/" + teamId + "/project/" + projectId + "/task"; //TODO UNDERSØG OM redirect:/ skal matche et endpoint i get mappingen kun eller om den også skal indrage request mapping for controlleren i endpoint
    }
}
