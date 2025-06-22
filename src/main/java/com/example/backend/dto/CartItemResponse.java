// src/main/java/com/example/backend/dto/CartItemResponse.java
package com.example.backend.dto;

import com.example.backend.model.CartItem;
import com.example.backend.model.Product;

// DTO for CartItem response (to avoid exposing full Product entity details unnecessarily)
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Double price;
    private String imageUrl;
    private Integer quantity;

    public CartItemResponse(CartItem cartItem) {
        this.id = cartItem.getId();
        Product product = cartItem.getProduct();
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice().doubleValue();
        this.imageUrl = product.getImageUrl();
        this.quantity = cartItem.getQuantity();
    }

    // Getters
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public Integer getQuantity() { return quantity; }

    // Setters (if needed for deserialization, but primarily for serialization here)
    public void setId(Long id) { this.id = id; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setPrice(Double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}