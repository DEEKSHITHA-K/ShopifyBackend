package com.example.backend.controller;

import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.backend.model.User;
import com.example.backend.service.UserService;

import java.util.List;
import java.util.Optional; // Import java.util.Optional

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService; // To get the User entity from UserDetails

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // Helper to get authenticated user's ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("User not authenticated.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Load the actual User entity to get the ID
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB."));
        return user.getId();
    }

    @PostMapping // Place a new order
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Long userId = getCurrentUserId();
            OrderResponse newOrder = orderService.createOrder(userId, orderRequest);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error creating order: " + e.getMessage());
            return ResponseEntity.badRequest().body(null); // Or more specific error response
        }
    }

    @GetMapping // Get all orders for the authenticated user
    public List<OrderResponse> getUserOrders() {
        Long userId = getCurrentUserId();
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/{orderId}") // Get a specific order by ID for the authenticated user
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        Optional<OrderResponse> orderResponse = orderService.getOrderByIdAndUser(orderId, userId);
        return orderResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)); // Corrected for type consistency
    }
}
