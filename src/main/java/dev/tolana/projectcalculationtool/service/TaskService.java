package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.TaskCreationDto;
import dev.tolana.projectcalculationtool.dto.TaskEditDto;
import dev.tolana.projectcalculationtool.dto.TaskViewDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.mapper.TaskDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
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

    public void createTask(String username, TaskCreationDto newTask) {
        Entity task =  taskDtoMapper.toEntity(newTask);
        taskRepository.createEntity(username, task);
    }

    public TaskViewDto getTaskToView(long taskId) {
        Entity task = taskRepository.getEntityOnId(taskId);
        return taskDtoMapper.toTaskViewDto(task);
    }

    public TaskEditDto getTaskToEdit(long taskId) {
        Entity task = taskRepository.getEntityOnId(taskId);
        return taskDtoMapper.toTaskEditDto(task);
    }

    public void deleteTask(long taskId) {
        taskRepository.deleteEntity(taskId);
    }

    public List<TaskViewDto> getChildren(long taskId) {
        List<Entity> taskList = taskRepository.getChildren(taskId);
        return taskDtoMapper.toTaskDtoViewList(taskList);
    }

    public List<Status> getStatusList() {
        return taskRepository.getStatusList();
    }

    public void editTask(TaskEditDto task) {
        Entity taskToEdit = taskDtoMapper.toEntity(task);
        taskRepository.editEntity(taskToEdit);
    }
}
