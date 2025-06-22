// src/main/java/com/ecommerceapp/backend/dto/OrderItemRequest.java
package com.example.backend.dto;

// DTO for individual item in an order request
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}