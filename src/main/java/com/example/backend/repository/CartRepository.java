package com.example.backend.repository;

import com.example.backend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JPA Repository for Cart entity
public interface CartRepository extends JpaRepository<Cart, Long> {
    // Find a cart by user ID
    Optional<Cart> findByUserId(Long userId);
}