package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskDtoMapper{

    public TaskDto convertToDto(Task task) {
        String taskName = task.getTaskName();
        String taskDescription = task.getTaskDescription();
        long projectId = task.getProjectId();
        LocalDate deadline = task.getDeadline();
        int estimatedHours = task.getEstimatedHours();
        int status = task.getStatus();
        long parentId = task.getParentId();

        return new TaskDto(taskName, taskDescription, projectId, deadline, estimatedHours, status, parentId);
    }

    public Task convertToTask(TaskDto taskDto) {
        return new Task(
                taskDto.taskName(),
                taskDto.taskDescription(),
                taskDto.projectId(),
                taskDto.deadline(),
                taskDto.estimatedHours(),
                taskDto.status(),
                taskDto.parentId()

        );
    }
}
