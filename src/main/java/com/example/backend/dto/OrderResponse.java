// src/main/java/com/example/backend/dto/OrderResponse.java
package com.example.backend.dto;

import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// DTO for returning order details to the frontend
public class OrderResponse {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private String shippingCountry;
    private List<OrderItemResponse> items;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.orderDate = order.getOrderDate();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.shippingAddress = order.getShippingAddress();
        this.shippingCity = order.getShippingCity();
        this.shippingPostalCode = order.getShippingPostalCode();
        this.shippingCountry = order.getShippingCountry();
        this.items = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getShippingAddress() { return shippingAddress; }
    public String getShippingCity() { return shippingCity; }
    public String getShippingPostalCode() { return shippingPostalCode; }
    public String getShippingCountry() { return shippingCountry; }
    public List<OrderItemResponse> getItems() { return items; }
}