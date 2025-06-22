// src/main/java/com/example/backend/exception/ResourceNotFoundException.java
package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Custom exception to indicate that a resource was not found (HTTP 404)
@ResponseStatus(HttpStatus.NOT_FOUND) // This annotation makes Spring automatically return a 404 status
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
