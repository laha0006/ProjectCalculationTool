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

    @GetMapping("/{projectId}/create")
    public String createForm(Model model, @PathVariable long projectId) {
        model.addAttribute("taskDto", new TaskDto("", "", projectId, LocalDateTime.now(), 0, 0, 0, -1));
        return "task/createTask";
    }

    @PostMapping("/create")
    public String createTask(@ModelAttribute TaskDto newTask, Authentication authentication) {
        String username = authentication.getName();
        taskService.createTask(newTask, username);

        long projectId = newTask.projectId();
        return "redirect:/task/overview/" + projectId; //TODO UNDERSØG OM redirect:/ skal matche et endpoint i get mappingen kun eller om den også skal indrage request mapping for controlleren i endpoint
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
}
