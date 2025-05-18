package com.example.taskly.Service;


import com.example.taskly.Domain.DTO.TaskDTO;
import com.example.taskly.Domain.Enums.TaskCategory;
import com.example.taskly.Domain.Enums.TaskPriority;
import com.example.taskly.Domain.Task;
import com.example.taskly.Domain.User;
import com.example.taskly.Repository.TaskRepository;
import com.example.taskly.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Autowired
    private OpenAiTaskCategorizer aiCategorizer;

    public Task createTask(TaskDTO dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow();

        Map<String, String> aiResult = aiCategorizer.categorize(
                dto.getTitle(), dto.getDescription(), dto.getDueDate().toString()
        );

        TaskCategory category = aiCategorizer.parseCategory(aiResult.get("category"));
        TaskPriority priority = aiCategorizer.parsePriority(aiResult.get("priority"));

        String finalDescription = dto.getDescription();
        if (finalDescription.trim().split("\\s+").length > 20) {
            finalDescription = aiCategorizer.rewriter(finalDescription);
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setDescription(finalDescription);
        task.setCategory(category);
        task.setPriority(priority);
        task.setCompleted(dto.isCompleted());
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public List<Task> getTasksForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return taskRepository.findByUserAndIsDeletedFalse(user);
    }

    public Task updateTask(Long id, TaskDTO dto) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCategory(dto.getCategory());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority());
        task.setUpdatedAt(LocalDateTime.now());
        task.setCompleted(dto.isCompleted());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setDeleted(true);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    public List<Task> searchTasks(String query, Long userId) {
        return taskRepository.searchTasks(query, userId);
    }

    public List<TaskCategory> getUserTaskCategories(Long userId) {
        return taskRepository.findDistinctCategoriesByUserId(userId);
    }

    public List<Task> getTasksByUserAndCategory(Long userId, TaskCategory category) {
        return taskRepository.findByUserIdAndCategoryAndIsDeletedFalse(userId, category);
    }
    public List<Task> getTasksSortedByPriority(Long userId) {
        return taskRepository.findAllByUserIdOrderByPriorityAndDueDateOrTitle(userId);
    }
    public List<Task> getArchivedTasks(Long userId) {
        return taskRepository.findByUserIdAndIsDeletedTrue(userId);
    }

    public Task restoreTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setDeleted(false);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task markTaskAsCompleted(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setCompleted(true);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public List<Task> getCompletedTasks(Long userId) {
        return taskRepository.findByUserIdAndIsCompletedTrueAndIsDeletedFalse(userId);
    }
    public List<Task> getIncompleteTasks(Long userId) {
        return taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(userId);
    }

    public List<Task> getOverdueTasks(Long userId) {
        return taskRepository.findOverdueTasks(userId);
    }
    public Map<String, Long> getTaskStats(Long userId) {
        long completed = taskRepository.countByUserIdAndIsCompletedTrueAndIsDeletedFalse(userId);
        long inProgress = taskRepository.countByUserIdAndIsCompletedFalseAndIsDeletedFalse(userId);
        long overdue = taskRepository.findOverdueTasks(userId).size();

        Map<String, Long> stats = new HashMap<>();
        stats.put("completed", completed);
        stats.put("inProgress", inProgress);
        stats.put("overdue", (long) overdue);
        return stats;
    }


}

