package dev.tolana.projectcalculationtool.controller;

import dev.tolana.projectcalculationtool.dto.CreateTaskFormDto;
import dev.tolana.projectcalculationtool.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/task")
public class TaskController {

    private TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("taskDto", new CreateTaskFormDto("", "", 0, LocalDate.now()));
        return "task/createTask";
    }

    @PostMapping("/create")
    public String createTask(CreateTaskFormDto newTask, Authentication authentication) {
        String username = authentication.getName();
        taskService.createTask(newTask, username);
        return "redirect:/"; //TODO redirect to a summary page.
    }
}
