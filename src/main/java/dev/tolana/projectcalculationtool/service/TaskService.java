package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.mapper.TaskDtoMapper;
import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskDtoMapper taskDtoMapper;
    public TaskService(TaskRepository taskRepository, TaskDtoMapper taskDtoMapper) {
        this.taskRepository = taskRepository;
        this.taskDtoMapper = taskDtoMapper;
    }

    public void createTask(TaskDto newTask, String username) {
        Task task =  taskDtoMapper.convertToTask(newTask);
        taskRepository.createParentTask(task, username);
    }

    public List<TaskDto> getAllProjectTasks(long projectId, String username) {
        List<Task> taskList = taskRepository.getAllProjectTasks(projectId, username);
        return taskDtoMapper.toTaskDtoList(taskList);
    }
}
