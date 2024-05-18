package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final EntityDtoMapper entityDtoMapper;
    public TaskService(TaskRepository taskRepository, EntityDtoMapper entityDtoMapper) {
        this.taskRepository = taskRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

    public void createTask(String username, TaskDto newTask) {
        Entity task =  entityDtoMapper.convertToEntity(newTask);
        taskRepository.createEntity(username, task);
    }

    public List<TaskDto> getAllProjectTasks(long projectId) {
        List<Entity> taskList = taskRepository.getAllEntitiesOnId(projectId);
        return entityDtoMapper.toTaskDtoList(taskList);
    }

    public TaskDto getTask(long taskId) {
        Entity task = taskRepository.getEntityOnId(taskId);
        return entityDtoMapper.convertToTaskDto((Task) task);
    }

    public void deleteTask(long taskId) {
        taskRepository.deleteEntity(taskId);
    }

    public List<TaskDto> getChildren(long taskId) {
        List<Entity> taskList = taskRepository.getChildren(taskId);
        return entityDtoMapper.toTaskDtoList(taskList);
    }
}
