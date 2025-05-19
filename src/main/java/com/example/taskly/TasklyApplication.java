package com.example.taskly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class TasklyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasklyApplication.class, args);
    }

}
