package com.example.taskly.Service;


import com.example.taskly.Domain.Enums.TaskCategory;
import com.example.taskly.Domain.Enums.TaskPriority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OpenAiTaskCategorizer {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public Map<String, String> categorize(String title, String description, String dueDate) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = String.format("""
                Given the following task:
                Title: %s
                Description: %s
                Due Date: %s
                
                Choose the most appropriate category from:    WORK,SHOPPING,GROCERIES,SCHOOL,FITNESS,STUDY,OTHER,
                FAMILY,REMINDER,HOMEWORK,GOALS.
                Choose the most appropriate priority from: LOW, MEDIUM, HIGH, URGENT.
                
                Respond in this exact format:
                Category: <CATEGORY>
                Priority: <PRIORITY>
                """, title, description, dueDate);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1",
                "messages", List.of(message),
                "temperature", 0.2
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("OpenAI-Organization", "org-fN0O5Fe7XXaolZruhnEb3Gay");


        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);

        String resultText = Optional.ofNullable(response.getBody())
                .map(bodyMap -> (List<Map<String, Object>>) bodyMap.get("choices"))
                .flatMap(choices -> choices.stream().findFirst())
                .map(choice -> (Map<String, Object>) choice.get("message"))
                .map(messageMap -> (String) messageMap.get("content"))
                .orElse("");

        Map<String, String> result = new HashMap<>();

        for (String line : resultText.split("\n")) {
            if (line.toLowerCase().startsWith("category:")) {
                result.put("category", line.replace("Category:", "").trim().toUpperCase());
            }
            if (line.toLowerCase().startsWith("priority:")) {
                result.put("priority", line.replace("Priority:", "").trim().toUpperCase());
            }
        }

        return result;
    }

    public TaskCategory parseCategory(String value) {
        try {
            return TaskCategory.valueOf(value);
        } catch (IllegalArgumentException e) {
            return TaskCategory.OTHER;
        }
    }

    public TaskPriority parsePriority(String value) {
        try {
            return TaskPriority.valueOf(value);
        } catch (IllegalArgumentException e) {
            return TaskPriority.MEDIUM;
        }
    }

    public String rewriter(String description) {
        if (description == null || description.trim().split("\\s+").length < 20) {
            return description;
        }

        RestTemplate restTemplate = new RestTemplate();

        String prompt = String.format("""
                    Please rewrite the following task description to make it clearer and more concise, 
                    grammarly correct
                    while preserving the original meaning:
                
                    "%s"
                """, description);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1",
                "messages", List.of(message),
                "temperature", 0.5
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", request, Map.class);

        return Optional.ofNullable(response.getBody())
                .map(bodyMap -> (List<Map<String, Object>>) bodyMap.get("choices"))
                .flatMap(choices -> choices.stream().findFirst())
                .map(choice -> (Map<String, Object>) choice.get("message"))
                .map(messageMap -> (String) messageMap.get("content"))
                .orElse(description);
    }

}
