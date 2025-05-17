package com.example.taskly.Repository;

import com.example.taskly.Domain.Enums.TaskCategory;
import com.example.taskly.Domain.Task;
import com.example.taskly.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserAndIsDeletedFalse(User user);

    @Query("SELECT t FROM Task t WHERE t.isDeleted = false " +
            "AND (:userId IS NULL OR t.user.id = :userId) " +
            "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Task> searchTasks(@Param("query") String query, @Param("userId") Long userId);

    @Query("SELECT DISTINCT t.category FROM Task t WHERE t.user.id = :userId AND t.isDeleted = false")
    List<TaskCategory> findDistinctCategoriesByUserId(@Param("userId") Long userId);

    List<Task> findByUserIdAndIsDeletedTrue(Long userId);

    List<Task> findByUserIdAndCategoryAndIsDeletedFalse(Long userId, TaskCategory category);

    @Query("SELECT t FROM Task t " +
            "WHERE t.user.id = :userId AND t.isDeleted = false " +
            "ORDER BY t.priority ASC, " +
            "CASE WHEN t.dueDate IS NULL THEN 1 ELSE 0 END, " +
            "t.dueDate ASC NULLS LAST, " +
            "t.title ASC")
    List<Task> findAllByUserIdOrderByPriorityAndDueDateOrTitle(@Param("userId") Long userId);

    List<Task> findByUserIdAndIsCompletedTrueAndIsDeletedFalse(Long userId);

    List<Task> findByUserIdAndIsCompletedFalseAndIsDeletedFalse(Long userId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.isCompleted = false AND t.isDeleted = false AND t.dueDate < CURRENT_TIMESTAMP")
    List<Task> findOverdueTasks(@Param("userId") Long userId);

    long countByUserIdAndIsCompletedTrueAndIsDeletedFalse(Long userId);

    long countByUserIdAndIsCompletedFalseAndIsDeletedFalse(Long userId);


}

