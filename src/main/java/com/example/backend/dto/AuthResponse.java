// src/main/java/com/example/backend/dto/AuthResponse.java
package com.example.backend.dto;

// DTO for login response
public class AuthResponse {
    private String jwtToken;
    private Long userId; // Include user ID in response
    private String username; // Or email

    public AuthResponse(String jwtToken, Long userId, String username) {
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.username = username;
    }

    public String getJwtToken() { return jwtToken; }
    public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}