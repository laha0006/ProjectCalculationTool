package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.ProjectStatsDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalculationService {

    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;

    public CalculationService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public ProjectStatsDto getProjectStats(long projectId) {
        List<Entity> allTasks = getAllTasksAndSubtasksFromProjectAndSubProjects(projectId);
        return getProjectStats(allTasks);
    }

    private static ProjectStatsDto getProjectStats(List<Entity> allTasks) {
        int totalEstiamtedHours = 0;
        int totalActualHours = 0;
        int tasksDone = 0;
        for (Entity entity : allTasks) {
            Task task = (Task) entity;
            totalEstiamtedHours += task.getEstimatedHours();
            totalActualHours += task.getActualHours();
            if ( task.getStatus() == Status.DONE) {
                tasksDone++;
            }
        }
        return new ProjectStatsDto(
                totalEstiamtedHours,
                totalActualHours,
                tasksDone
        );
    }


    private List<Entity> getAll(long projectId) {
        List<Entity> tasks = getAllTasksAndSubTasks(projectId);
        List<Entity> subProjects = projectRepository.getChildren(projectId);
        for (Entity subProject : subProjects) {
            tasks.addAll(getAllTasksAndSubTasks(subProject.getId()));
        }
        return tasks;
    }

    private List<Entity> getAllTasksAndSubTasksFromProject(long projectId) {
        List<Entity> tasks = projectRepository.getChildren(projectId);
        List<Entity> subTasks = new ArrayList<>();
        for (Entity task : tasks) {
            long taskId = task.getId();
            subTasks.addAll(taskRepository.getChildren(taskId));
        }
        tasks.addAll(subTasks);
        return tasks;
    }
//    get tasks
//    get sub-tasks
//    get sub-projects
//    get tasks of sub projects
}
