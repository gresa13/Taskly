package com.example.taskly.Domain.DTO;

import com.example.taskly.Domain.Enums.TaskCategory;
import com.example.taskly.Domain.Enums.TaskPriority;

import java.time.LocalDateTime;

public class TaskDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskCategory category;
    private TaskPriority priority;
    private Long userId;
    private boolean isCompleted;


    public TaskDTO() {
    }

    public TaskDTO(String title, String description, LocalDateTime dueDate,
                   TaskCategory category, TaskPriority priority, Long userId, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.userId = userId;
        this.isCompleted = isCompleted;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }


}



