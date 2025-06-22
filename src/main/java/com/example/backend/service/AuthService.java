package com.example.backend.service;

import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager; // For Spring Security authentication
    @Autowired
    private UserDetailsService userDetailsService; // Our custom user details service
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse authenticateUser(AuthRequest authRequest) throws AuthenticationException {
        // Authenticate the user using Spring Security's AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        // If authentication succeeds, load UserDetails and generate JWT
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        final User user = userRepository.findByEmail(authRequest.getEmail())
                                        .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        return new AuthResponse(jwt, user.getId(), user.getEmail());
    }

    public AuthResponse registerUser(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already registered: " + registerRequest.getEmail());
        }

        // Create new user entity
        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Hash password

        User savedUser = userRepository.save(newUser);

        // Generate JWT immediately after registration (optional, can be separate login step)
        final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getEmail());
    }
}