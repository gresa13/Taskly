package com.example.taskly.Service;

import com.example.taskly.Domain.Task;
import com.example.taskly.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationScheduler {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    //everyday at 8 am
    @Scheduled(cron = "0 0 8 * * *")
    public void sendNotification() {
        LocalDate today = LocalDate.now();

        List<Task> tasks = taskRepository.findAll();

        for (Task task : tasks) {
            LocalDateTime dueDate = task.getDueDate() != null ? task.getDueDate() : task.getCreatedAt().plusDays(10);

            if (dueDate.minusDays(1).isEqual(today.atStartOfDay())) {
                emailService.sendEmail(
                        task.getUser().getEmail(),
                        "Reminder: Task '" + task.getTitle() + "' is due tomorrow",
                        "Your task is due on " + dueDate
                );
            }


            if (dueDate.plusDays(1).isEqual(today.atStartOfDay())) {
                emailService.sendEmail(
                        task.getUser().getEmail(),
                        "Overdue : Task '" + task.getTitle() + "' was due yesterday",
                        "Your task was due on " + dueDate + ". Please complete it."
                );
            }
        }
    }
}
