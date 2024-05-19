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
            System.out.println("is empty");
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
        double actualOverAllotedHours = ((double) totalActualHours / alottedHours) * 100;
        double actualOverEstimatedHours = ((double) totalActualHours / totalEstimatedHours) * 100;
        double estiamtedOverAllottedHours = ((double) totalEstimatedHours / alottedHours) * 100;
        return new ProjectStatsDto(
                alottedHours,
                totalEstimatedHours,
                totalActualHours,
                totalTaskCount,
                taskDone,
                actualOverAllotedHours,
                actualOverEstimatedHours,
                estiamtedOverAllottedHours,
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
        int alottedHours = project.getAllottedHours();
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
        double actualOverAllotedHours = ((double) totalActualHours / alottedHours) * 100;
        double actualOverEstimatedHours = ((double) totalActualHours / totalEstimatedHours) * 100;
        double estiamtedOverAllottedHours = ((double) totalEstimatedHours / alottedHours) * 100;
        return new ProjectStatsDto(
                alottedHours,
                totalEstimatedHours,
                totalActualHours,
                totalTaskCount,
                taskDone,
                actualOverAllotedHours,
                actualOverEstimatedHours,
                estiamtedOverAllottedHours,
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
        System.out.println("TASK ID IN GET TASK STATS: " + task.getId());
        List<Entity> subTasks = taskRepository.getChildren(task.getId());
        System.out.println("SubTask Size: " + subTasks.size());
        if (subTasks.isEmpty()) {
            System.out.println("SUBTASKS EMPTY??? WAT");
            totalEstimatedHours = task.getEstimatedHours();
            totalActualHours = task.getActualHours();
        } else {
            subTasksTotal = subTasks.size();
            for (Entity entitySubTask : subTasks) {
                Task subTask = (Task) entitySubTask;
                totalEstimatedHours += subTask.getEstimatedHours();
                totalActualHours += subTask.getActualHours();
                System.out.println("total actual: " + subTask.getActualHours());
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
//    private void addSubProjectStats(Map<Long, ProjectStatsDto> subProjectMap, long projectId) {
//        List<Project> subProjects = projectRepository.getSubProjects(projectId);
//        for (Project subProject : subProjects) {
//            List<Entity> allSubProjectTasks = getAllTasksAndSubTasksFromProject(subProject.getId());
//            ProjectStatsDto subProjectStats = getStatsForProject(allSubProjectTasks);
//            subProjectMap.put(subProject.getId(), subProjectStats);
//        }
//    }
//
//
//    public ProjectStatsDto getAllProjectStats(long projectId) {
//        List<Entity> allTasks = getAllTasksAndSubtasksFromProjectAndSubProjects(projectId);
//        ProjectStatsDto allProjectStats = getStatsForProject(allTasks);
//        addSubProjectStats(allProjectStats.subProjects(), projectId);
//        return
//    }
//
//    private ProjectStatsDto getStatsForProject(List<Entity> tasks) {
//        int totalEstiamtedHours = 0;
//        int totalActualHours = 0;
//        int tasksDone = 0;
//        for (Entity entity : tasks) {
//            Task task = (Task) entity;
//            totalEstiamtedHours += task.getEstimatedHours();
//            totalActualHours += task.getActualHours();
//            if ( task.getStatus() == Status.DONE) {
//                tasksDone++;
//            }
//        }
//        return new ProjectStatsDto(
//                totalEstiamtedHours,
//                totalActualHours,
//                tasksDone,
//                new HashMap<>()
//        );
//    }
//
//
//    private List<Entity> getAllTasksAndSubtasksFromProjectAndSubProjects(long projectId) {
//        List<Entity> tasks = getAllTasksAndSubTasksFromProject(projectId);
//        List<Project> subProjects = projectRepository.getSubProjects(projectId);
//        for (Project subProject : subProjects) {
//            tasks.addAll(getAllTasksAndSubTasksFromProject(subProject.getId()));
//        }
//        return tasks;
//    }
//
//    private List<Entity> getAllTasksAndSubTasksFromProject(long projectId) {
//        List<Entity> tasks = projectRepository.getChildren(projectId);
//        List<Entity> subTasks = new ArrayList<>();
//        for (Entity task : tasks) {
//            long taskId = task.getId();
//            subTasks.addAll(taskRepository.getChildren(taskId));
//        }
//        tasks.addAll(subTasks);
//        return tasks;
//    }
//    get tasks
//    get sub-tasks
//    get sub-projects
//    get tasks of sub projects
}
