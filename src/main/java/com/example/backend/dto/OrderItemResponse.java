// src/main/java/com/example/backend/dto/OrderItemResponse.java
package com.example.backend.dto;

import com.example.backend.model.OrderItem;
import java.math.BigDecimal;

// DTO for individual item within an OrderResponse
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String productImageUrl; // Added for display in frontend
    private Integer quantity;
    private BigDecimal priceAtOrder;

    public OrderItemResponse(OrderItem orderItem) {
        this.productId = orderItem.getProduct().getId();
        this.productName = orderItem.getProduct().getName();
        this.productImageUrl = orderItem.getProduct().getImageUrl();
        this.quantity = orderItem.getQuantity();
        this.priceAtOrder = orderItem.getPriceAtOrder();
    }

    // Getters
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImageUrl() { return productImageUrl; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPriceAtOrder() { return priceAtOrder; }
}