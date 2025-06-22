package com.example.backend.repository;

import com.example.backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JPA Repository for CartItem entity
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Find a cart item by cart ID and product ID
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}