// src/main/java/com/example/backend/dto/AuthRequest.java
package com.example.backend.dto;

// DTO for login request
public class AuthRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}


