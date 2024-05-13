package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskDtoMapper{

    public TaskDto convertToDto(Task task) {
        String taskName = task.getTaskName();
        String taskDescription = task.getTaskDescription();
        long projectId = task.getProjectId();
        LocalDateTime deadline = task.getDeadline();
        int estimatedHours = task.getEstimatedHours();
        int status = task.getStatus();
        long parentId = task.getParentId();
        long taskId = task.getTaskId();

        return new TaskDto(taskName, taskDescription, projectId, deadline, estimatedHours, status, parentId, taskId);
    }

    public Task convertToTask(TaskDto taskDto) {
        return new Task(
                taskDto.taskName(),
                taskDto.taskDescription(),
                taskDto.projectId(),
                taskDto.deadline(),
                taskDto.estimatedHours(),
                taskDto.status(),
                taskDto.parentId(),
                taskDto.taskId()
        );
    }

    public List<TaskDto> toTaskDtoList(List<Task> taskList) {
        List<TaskDto> taskDtoList = new ArrayList<>();

        for (Task task : taskList) {
            TaskDto taskDto = new TaskDto(
                    task.getTaskName(),
                    task.getTaskDescription(),
                    task.getProjectId(),
                    task.getDeadline(),
                    task.getEstimatedHours(),
                    task.getStatus(),
                    task.getParentId(),
                    task.getTaskId()
            );
            taskDtoList.add(taskDto);
        }

        return taskDtoList;
    }
}
