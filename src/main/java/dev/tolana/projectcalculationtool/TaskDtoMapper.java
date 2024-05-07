package dev.tolana.projectcalculationtool;

import dev.tolana.projectcalculationtool.dto.TaskDto;
import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskDtoMapper{

    public TaskDto convertToDto(Task task) {
        String taskName = task.getTaskName();
        String taskDescription = task.getTaskDescription();
        LocalDate deadline = task.getDeadline();
        int estimatedHours = task.getEstimatedHours();
        int status = task.getStatus();

        return new TaskDto(taskName, taskDescription, deadline, estimatedHours, status);
    }

    public Task convertToTask(TaskDto taskDto) {
        return new Task(
                taskDto.taskName(),
                taskDto.taskDescription(),
                taskDto.deadline(),
                taskDto.estimatedHours(),
                taskDto.status()
        );
    }
}
