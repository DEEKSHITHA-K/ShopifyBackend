// src/main/java/com/example/backend/service/CartService.java
package com.example.backend.service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Cart;
import com.example.backend.model.CartItem;
import com.example.backend.model.Product;
import com.example.backend.model.User;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.dto.AddToCartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retrieves the cart for a given user ID. If the cart does not exist, it creates a new one.
     * @param userId The ID of the user.
     * @return The user's cart.
     */
    @Transactional
    public Cart getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        return existingCart.orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    /**
     * Adds a product to the user's cart or updates its quantity.
     * @param userId The ID of the user.
     * @param productId The ID of the product to add.
     * @param quantity The quantity to add (can be negative to remove quantity).
     * @return The updated cart item.
     * @throws ResourceNotFoundException if product or user not found.
     * @throws IllegalArgumentException if quantity is invalid.
     */
    @Transactional
    public CartItem addProductToCart(Long userId, Long productId, int quantity) {
        if (quantity == 0) {
            throw new IllegalArgumentException("Quantity cannot be zero.");
        }

        Cart cart = getCartByUserId(userId); // Ensures a cart exists for the user
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity <= 0) {
                // If new quantity is 0 or less, remove the item
                cartItemRepository.delete(cartItem);
                return null; // Indicate item was removed
            }
            cartItem.setQuantity(newQuantity);
        } else {
            if (quantity < 0) {
                throw new IllegalArgumentException("Cannot remove negative quantity of a non-existent item.");
            }
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getCartItems().add(cartItem); // Add to cart's list for consistency
        }

        return cartItemRepository.save(cartItem);
    }

    /**
     * Removes a product completely from the cart or reduces its quantity.
     * @param userId The ID of the user.
     * @param productId The ID of the product to remove.
     * @param quantityToRemove The quantity to remove. If -1, removes all.
     * @throws ResourceNotFoundException if cart item not found.
     * @throws IllegalArgumentException if quantityToRemove is invalid.
     */
    @Transactional
    public void removeProductFromCart(Long userId, Long productId, int quantityToRemove) {
        Cart cart = getCartByUserId(userId); // Ensures cart exists

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart with id: " + productId));

        if (quantityToRemove == -1 || quantityToRemove >= cartItem.getQuantity()) {
            // Remove all of this item
            cartItemRepository.delete(cartItem);
        } else if (quantityToRemove > 0 && quantityToRemove < cartItem.getQuantity()) {
            // Reduce quantity
            cartItem.setQuantity(cartItem.getQuantity() - quantityToRemove);
            cartItemRepository.save(cartItem);
        } else {
            throw new IllegalArgumentException("Invalid quantity to remove: " + quantityToRemove);
        }
    }

    /**
     * Clears all items from the user's cart.
     * @param userId The ID of the user.
     */
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear(); // Clear the list in the entity as well
        cartRepository.save(cart); // Save the updated cart (optional, as cascade should handle it)
    }
}