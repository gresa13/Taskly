package com.example.taskly.Controller;


import com.example.taskly.Domain.DTO.TaskDTO;
import com.example.taskly.Domain.Enums.TaskCategory;
import com.example.taskly.Domain.Task;
import com.example.taskly.Service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTask(@RequestBody TaskDTO dto) {
        return taskService.createTask(dto);
    }


    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody TaskDTO dto) {
        return taskService.updateTask(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @GetMapping("/tasks/search")
    public List<Task> searchTasks(
            @RequestParam String query,
            @RequestParam(required = false) Long userId) {
        return taskService.searchTasks(query, userId);
    }
    @GetMapping
    public ResponseEntity<?> getTasksOrCategories(
            @RequestParam Long userId,
            @RequestParam(required = false) TaskCategory category) {

        if (category != null) {
            List<Task> tasks = taskService.getTasksByUserAndCategory(userId, category);
            return ResponseEntity.ok(tasks);
        } else {
            List<TaskCategory> categories = taskService.getUserTaskCategories(userId);
            return ResponseEntity.ok(categories);
        }
    }

    @GetMapping("/priority")
    public List<Task> getTasksSortedByPriority(@RequestParam Long userId) {
        return taskService.getTasksSortedByPriority(userId);
    }
    @GetMapping("/tasks/archived")
    public List<Task> getArchivedTasks(@RequestParam Long userId) {
        return taskService.getArchivedTasks(userId);
    }

    @PostMapping("/tasks/{id}/restore")
    public ResponseEntity<Task> restoreTask(@PathVariable Long id) {
        Task restoredTask = taskService.restoreTask(id);
        return ResponseEntity.ok(restoredTask);
    }

    @PostMapping("/tasks/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.markTaskAsCompleted(id));
    }

    @GetMapping("/tasks/completed")
    public List<Task> getCompletedTasks(@RequestParam Long userId) {
        return taskService.getCompletedTasks(userId);
    }

    @GetMapping("/tasks/incomplete")
    public List<Task> getIncompleteTasks(@RequestParam Long userId) {
        return taskService.getIncompleteTasks(userId);
    }

    @GetMapping("/tasks/overdue")
    public List<Task> getOverdueTasks(@RequestParam Long userId) {
        return taskService.getOverdueTasks(userId);
    }

    @GetMapping("/tasks/stats")
    public Map<String, Long> getTaskStats(@RequestParam Long userId) {
        return taskService.getTaskStats(userId);
    }





}
