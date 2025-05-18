package com.example.taskly.Domain.DTO;

public class AuthResponseDTO {
    private String token;

    // Default constructor
    public AuthResponseDTO() {
    }

    // Parameterized constructor
    public AuthResponseDTO(String token) {
        this.token = token;
    }

    // Getter and Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
