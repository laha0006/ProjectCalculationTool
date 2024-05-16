package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.mapper.EntityDtoMapper;
import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.ResourceEntityCrudOperations;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("@auth.hasTaskAccess(#newTask.projectId(), " +
            "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_CREATE)")
    public void createTask(String username, TaskDto newTask) {
        Entity task =  entityDtoMapper.convertToEntity(newTask);
        taskRepository.createEntity(username, task);
    }

    @PreAuthorize("@auth.hasTaskAccess(#projectId(), " +
            "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_READ)")

    public List<TaskDto> getAllProjectTasks(long projectId) {
        List<Entity> taskList = taskRepository.getAllEntitiesOnId(projectId);
        return entityDtoMapper.toTaskDtoList(taskList);
    }

    @PreAuthorize("@auth.hasTaskAccess(#taskId, " +
            "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_READ)")
    public TaskDto getTaskOnId(long taskId) {
        Entity task = taskRepository.getEntityOnId(taskId);
        return entityDtoMapper.convertToTaskDto((Task) task);
    }
}
