package com.example.backend.controller;

import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.BadCredentialsException; // Import specific exception

@RestController
@RequestMapping("/api/auth") // Base path for authentication endpoints
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            AuthResponse authResponse = authService.authenticateUser(authRequest);
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            // Return 401 Unauthorized for bad credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password");
        } catch (Exception e) {
            // Catch other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse authResponse = authService.registerUser(registerRequest);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Handle specific errors like email already registered
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // Catch other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration error: " + e.getMessage());
        }
    }
}