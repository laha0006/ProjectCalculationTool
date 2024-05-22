package dev.tolana.projectcalculationtool.mapper;

import dev.tolana.projectcalculationtool.dto.TaskCreationDto;
import dev.tolana.projectcalculationtool.dto.TaskEditDto;
import dev.tolana.projectcalculationtool.dto.TaskViewDto;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskDtoMapper {

    public TaskViewDto toTaskViewDto(Entity entity) {
        return new TaskViewDto(
                entity.getName(),
                entity.getDescription(),
                entity.getId(),
                ((Task)entity).getDeadline(),
                ((Task) entity).getEstimatedHours(),
                ((Task) entity).getActualHours(),
                ((Task) entity).getStatus()
        );
    }

    public List<TaskViewDto> toTaskDtoViewList(List<Entity> taskList) {
        List<TaskViewDto> taskViewDtoList = new ArrayList<>();

        for (Entity entity : taskList) {
            taskViewDtoList.add(toTaskViewDto(entity));
        }
        return taskViewDtoList;
    }

    public TaskEditDto toTaskEditDto(Entity task) {
        return new TaskEditDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                ((Task)task).getParentId(),
                ((Task) task).getDeadline(),
                ((Task) task).getEstimatedHours(),
                ((Task) task).getActualHours(),
                ((Task) task).getStatus()
        );
    }

    public Entity toEntity(TaskCreationDto taskToCreate) {

        return new Task(
                taskToCreate.taskName(),
                taskToCreate.description(),
                taskToCreate.projectId(),
                taskToCreate.parentId(),
                taskToCreate.deadline(),
                taskToCreate.estimatedHours()
        );
    }

    public Entity toEntity(TaskEditDto taskToEdit) {

        return new Task(
                taskToEdit.id(),
                taskToEdit.taskName(),
                taskToEdit.description(),
                taskToEdit.parentId(),
                taskToEdit.deadline(),
                taskToEdit.estimatedHours(),
                taskToEdit.actualHours(),
                taskToEdit.status()
        );
    }
}