package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity // This enables Spring Security's web security features
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless APIs (common for REST)
            .cors(cors -> {}) // Enable CORS - Spring will automatically pick up your WebMvcConfigurer (WebConfig)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions for REST APIs
            .authorizeHttpRequests(authorize -> authorize // Configure authorization rules
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // IMPORTANT: Allow all OPTIONS requests (CORS preflight)
                .requestMatchers("/api/auth/**").permitAll() // Allow requests to /api/auth/login and other auth endpoints without authentication
                // Add more permitAll() rules for other public endpoints if you have them, e.g.,
                // .requestMatchers("/api/products/public").permitAll()
                .anyRequest().authenticated() // All other requests require authentication
            );
        return http.build();
    }

    // You might also need to define a PasswordEncoder and an AuthenticationProvider/Manager here
    // For example:
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
}