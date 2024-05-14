package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.model.Task;

import java.util.List;

public interface TaskRepository {

    boolean createTask(Task task, String username);

    boolean deleteTask(long taskId, String username);

    boolean editTask(long taskId, String username);

    Task getTaskOnId(long taskId);

    List<Task> getAllTasks(String username);

    List<Task> getAllProjectTasks(long projectId, String username);
}
