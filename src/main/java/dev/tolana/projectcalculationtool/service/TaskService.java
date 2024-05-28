package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.TaskCreationDto;
import dev.tolana.projectcalculationtool.dto.TaskEditDto;
import dev.tolana.projectcalculationtool.dto.TaskViewDto;
import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.mapper.TaskDtoMapper;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskDtoMapper taskDtoMapper;

    public TaskService(TaskRepository taskRepository, TaskDtoMapper taskDtoMapper) {
        this.taskRepository = taskRepository;
        this.taskDtoMapper = taskDtoMapper;
    }


    @PreAuthorize("@auth.hasProjectAccess(#newTask.parentId(), " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_CREATE)")
    public void createTask(String username, TaskCreationDto newTask) {
        Entity task =  taskDtoMapper.toEntity(newTask);
        taskRepository.createEntity(username, task);
    }

    @PreAuthorize("@auth.hasTaskAccess(#taskId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_READ)")
    public TaskViewDto getTaskToView(long taskId) {
        Entity task = taskRepository.getEntityOnId(taskId);
        return taskDtoMapper.toTaskViewDto(task);
    }

    @PreAuthorize("@auth.hasTaskAccess(#taskId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_EDIT)")
    public TaskEditDto getTaskToEdit(long taskId) {
        Entity task = taskRepository.getEntityOnId(taskId);
        return taskDtoMapper.toTaskEditDto(task);
    }

    @PreAuthorize("@auth.hasTaskAccess(#taskId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_DELETE)")
    public void deleteTask(long taskId) {
        taskRepository.deleteEntity(taskId);
    }

    @PostFilter("@auth.hasTaskAccess(filterObject.id(), " +
                "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_READ)")
    public List<TaskViewDto> getChildren(long taskId) {
        List<Entity> taskList = taskRepository.getChildren(taskId);
        return taskDtoMapper.toTaskDtoViewList(taskList);
    }

    public List<Status> getStatusList() {
        return taskRepository.getStatusList();
    }


    @PreAuthorize("@auth.hasTaskAccess(#task.id(), " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_EDIT)")
    public void editTask(TaskEditDto task) {
        Entity taskToEdit = taskDtoMapper.toEntity(task);
        taskRepository.editEntity(taskToEdit);
    }

    public List<UserEntityRoleDto> getUsersFromTaskId(long taskId) {
        List<UserEntityRoleDto> scrubbedUsers = new ArrayList<>();

        List<UserEntityRoleDto> users = taskRepository.getUsersFromEntityId(taskId);

        for (UserEntityRoleDto user : users) {
            if (!scrubbedUsers.contains(user)) {
                scrubbedUsers.add(user);
            }
        }

        Collections.sort(scrubbedUsers);
        return scrubbedUsers;
    }

    public List<UserEntityRoleDto> getUsersFromProject(long projectId, long taskId) {
        List<UserEntityRoleDto> scrubbedUsers = new ArrayList<>();

        List<UserEntityRoleDto> users = taskRepository.getUsersFromParentIdAndEntityId(
                taskId, projectId);

        for (UserEntityRoleDto user : users) {
            if (!scrubbedUsers.contains(user)) {
                scrubbedUsers.add(user);
            }
        }

        Collections.sort(scrubbedUsers);
        return scrubbedUsers;
    }

    @PreAuthorize("@auth.hasTaskAccess(#taskId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_READ)") // task_read is sufficient since self-assignment is allowed.
    public void assignMemberToTask(String username, long taskId) {
        taskRepository.assignMemberToEntity(taskId, username);
    }

    @PreAuthorize("@auth.hasTaskAccess(#taskId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_KICK)")
    public void kickMemberFromTask(long taskId, String username) {
        taskRepository.kickMember(taskId, username);
    }

    public void removeSelfFromTask(long taskId) {
        taskRepository.removeSelfFromTask(taskId);
    }

    @PreAuthorize("@auth.hasTaskAccess(#taskId, " +
                  "T(dev.tolana.projectcalculationtool.enums.Permission).TASK_EDIT)")
    public void promoteMemberToAdmin(long taskId, String username) {
        taskRepository.promoteMemberToAdmin(taskId, username);
    }
}
