package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.CreateTaskFormDto;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(CreateTaskFormDto newTask, String username) {
        taskRepository.createTask(newTask, username);
    }
}
