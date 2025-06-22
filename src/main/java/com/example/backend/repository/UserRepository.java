// src/main/java/com/example/backend/repository/UserRepository.java
package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Custom method to find user by email
    Boolean existsByEmail(String email); // Check if email already exists
}