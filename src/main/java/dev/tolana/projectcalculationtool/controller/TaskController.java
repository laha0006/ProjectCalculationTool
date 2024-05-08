package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/task")
public class TaskController {

    private TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{projectId}/create")
    public String createForm(Model model, @PathVariable long projectId) {
        model.addAttribute("taskDto", new TaskDto("", "", projectId, LocalDate.now(), 0, 0, 0));
        return "task/createTask";
    }

    @PostMapping("/create")
    public String createTask(@ModelAttribute TaskDto newTask, Authentication authentication) {
        String username = authentication.getName();
        taskService.createTask(newTask, username);
        return "redirect:/dashboard"; //TODO redirect to a summary page.
    }
}
