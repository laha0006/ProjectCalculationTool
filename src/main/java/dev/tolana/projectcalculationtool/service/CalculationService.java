package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.ProjectStatsDto;
import dev.tolana.projectcalculationtool.dto.TaskStatsDto;
import dev.tolana.projectcalculationtool.enums.Status;
import dev.tolana.projectcalculationtool.model.Entity;
import dev.tolana.projectcalculationtool.model.Project;
import dev.tolana.projectcalculationtool.model.Task;
import dev.tolana.projectcalculationtool.repository.ProjectRepository;
import dev.tolana.projectcalculationtool.repository.TaskRepository;
import dev.tolana.projectcalculationtool.repository.impl.JdbcProjectRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalculationService {

    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;

    public CalculationService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public ProjectStatsDto getProjectStats(long projectId) {
        Project project = (Project) projectRepository.getEntityOnId(projectId);
        List<Project> subProjects = projectRepository.getSubProjects(projectId);
        ProjectStatsDto projectStats = getProjectStats(project);
        if (subProjects.isEmpty()) {
            return projectStats;
        }
        Map<Long, ProjectStatsDto> subProjectStatsMap = new HashMap<>();
        int alottedHours = project.getAllottedHours();
        int totalEstimatedHours = 0;
        int totalActualHours = 0;
        int totalTaskCount = 0;
        int taskDone = 0;
        List<ProjectStatsDto> projects = new ArrayList<>();
        projects.add(projectStats);
        for (Entity subProject : subProjects) {
            ProjectStatsDto subProjectStats = getProjectStats(subProject);
            projects.add(subProjectStats);
            subProjectStatsMap.put(subProject.getId(), subProjectStats);
        }
        for (ProjectStatsDto projectStatsDto : projects) {
            totalEstimatedHours += projectStatsDto.totalEstimatedHours();
            totalActualHours += projectStatsDto.totalActualHours();
            totalTaskCount += projectStatsDto.totalTaskCount();
            taskDone += projectStatsDto.tasksDone();
        }
        double actualOverAllottedHours = ((double) totalActualHours / alottedHours) * 100;
        double actualOverEstimatedHours = ((double) totalActualHours / totalEstimatedHours) * 100;
        double estimatedOverAllottedHours = ((double) totalEstimatedHours / alottedHours) * 100;
        return new ProjectStatsDto(
                alottedHours,
                totalEstimatedHours,
                totalActualHours,
                totalTaskCount,
                taskDone,
                actualOverAllottedHours,
                actualOverEstimatedHours,
                estimatedOverAllottedHours,
                projectStats.tasksMap(),
                subProjectStatsMap
        );

    }


    private ProjectStatsDto getProjectStats(Entity entity) {
        Project project = (Project) entity;
        List<Entity> tasks = projectRepository.getChildren(project.getId());
        Map<Long, TaskStatsDto> taskStatsMap = new HashMap<>();
        for (Entity task : tasks) {
            taskStatsMap.put(task.getId(), getTaskStats(task));
        }
        int allottedHours = project.getAllottedHours();
        int totalEstimatedHours = 0;
        int totalActualHours = 0;
        int totalTaskCount = tasks.size();
        int taskDone = 0;
        for (TaskStatsDto taskStats : taskStatsMap.values()) {
            totalEstimatedHours += taskStats.totalEstimatedHours();
            totalActualHours += taskStats.totalActualHours();
            if (taskStats.isDone()) {
                taskDone++;
            }
        }
        double actualOverAllottedHours = ((double) totalActualHours / allottedHours) * 100;
        double actualOverEstimatedHours = ((double) totalActualHours / totalEstimatedHours) * 100;
        double estimatedOverAllottedHours = ((double) totalEstimatedHours / allottedHours) * 100;
        return new ProjectStatsDto(
                allottedHours,
                totalEstimatedHours,
                totalActualHours,
                totalTaskCount,
                taskDone,
                actualOverAllottedHours,
                actualOverEstimatedHours,
                estimatedOverAllottedHours,
                taskStatsMap,
                new HashMap<>()
        );
    }

    private TaskStatsDto getTaskStats(Entity entity) {
        Task task = (Task) entity;
        boolean isDone = task.getStatus() == Status.DONE;
        int estimatedHours = 0;
        int totalEstimatedHours = 0;
        int totalActualHours = 0;
        int subTasksTotal = 0;
        int subTasksDone = 0;
        estimatedHours = task.getEstimatedHours();

        List<Entity> subTasks = taskRepository.getChildren(task.getId());

        if (subTasks.isEmpty()) {
            totalEstimatedHours = task.getEstimatedHours();
            totalActualHours = task.getActualHours();

        } else {
            subTasksTotal = subTasks.size();
            for (Entity entitySubTask : subTasks) {
                Task subTask = (Task) entitySubTask;
                totalEstimatedHours += subTask.getEstimatedHours();
                totalActualHours += subTask.getActualHours();

                if (subTask.getStatus() == Status.DONE) {
                    subTasksDone++;
                }
            }
        }

        return new TaskStatsDto(
                isDone,
                estimatedHours,
                totalEstimatedHours,
                totalActualHours,
                subTasksTotal,
                subTasksDone
        );
    }
}
