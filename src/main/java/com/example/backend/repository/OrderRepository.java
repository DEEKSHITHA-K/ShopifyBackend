// src/main/java/com/ecommerceapp/backend/repository/OrderRepository.java
package com.example.backend.repository;

import com.example.backend.model.Order;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user); // Find orders for a specific user, ordered by date
}