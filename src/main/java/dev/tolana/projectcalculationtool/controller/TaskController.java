package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/overview/{projectId}")
    public String getTaskOverview(Model model,
                                  Authentication authentication,
                                  @PathVariable long projectId) {

        String username = authentication.getName();
        List<TaskDto> taskList = taskService.getAllProjectTasks(projectId, username);
        model.addAttribute("projectId", projectId);

        model.addAttribute("taskList", taskList);
        return "task/viewAllTasks";
    }

    @GetMapping("/{projectId}/create")
    public String sendCreateForm(Model model, @PathVariable long projectId) {
        model.addAttribute("taskDto", new TaskDto("", "", projectId, LocalDateTime.now(), 0, 0, 0, -1));
        model.addAttribute("projectId", projectId);
        return "task/createTask";
    }

    @GetMapping("/{taskId}/create/subtask/{projectId}")
    public String sendCreateSubTaskForm(@PathVariable long taskId,
                                        @PathVariable long projectId,
                                        Model model) {
        model.addAttribute("taskDto", new TaskDto("", "", projectId, LocalDateTime.now(), 0, 0, taskId, -1));
        model.addAttribute("projectId", projectId);
        return "task/createTask";
    }

    @PostMapping("/{projectId}/create")
    public String createTask(@PathVariable long projectId,
                             @ModelAttribute TaskDto newTask,
                             Authentication authentication) {

        String username = authentication.getName();
        taskService.createTask(newTask, username);

        return "redirect:/task/overview/" + projectId; //TODO UNDERSØG OM redirect:/ skal matche et endpoint i get mappingen kun eller om den også skal indrage request mapping for controlleren i endpoint
    }
}
