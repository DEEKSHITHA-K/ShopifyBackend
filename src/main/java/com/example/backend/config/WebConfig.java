// src/main/java/com/example/backend/config/WebConfig.java
package com.example.backend.config; // <-- Make sure this package matches your project's base package

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // This annotation tells Spring that this class provides configuration
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply CORS to all endpoints under /api
                .allowedOrigins("https://third-booth-454811-d3.web.app", "https://third-booth-454811-d3.firebaseapp.com", "https://shopifyappweb.netlify.app") // <-- TEMPORARY: WILL BE UPDATED LATER with your frontend Cloud Run URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow these HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow sending of cookies/authentication headers (if applicable, good for JWT)
    }
}
