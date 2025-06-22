// src/main/java/com/example/backend/dto/AddToCartRequest.java
package com.example.backend.dto;

// DTO for adding a product to cart request
public class AddToCartRequest {
    private Long productId;
    private Integer quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}