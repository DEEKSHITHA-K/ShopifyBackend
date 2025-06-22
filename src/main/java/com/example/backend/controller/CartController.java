// src/main/java/com/example/backend/controller/CartController.java
package com.example.backend.controller;

import com.example.backend.model.CartItem;
import com.example.backend.service.CartService;
import com.example.backend.dto.AddToCartRequest;
import com.example.backend.dto.CartItemResponse;
import com.example.backend.service.UserService; // Import UserService
import com.example.backend.exception.ResourceNotFoundException; // Import ResourceNotFoundException
import com.example.backend.model.User; // Import User model

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // For getting auth principal
import org.springframework.security.core.context.SecurityContextHolder; // For getting auth principal
import org.springframework.security.core.userdetails.UserDetails; // For getting auth principal
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Still useful for general principal access, but we'll use SecurityContextHolder directly for UserDetails
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService; // Inject UserService to get User entity

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    // Helper to get authenticated user's ID
    // This correctly extracts the email from the authenticated principal
    // and then uses the UserService to find the corresponding User entity's ID.
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("User not authenticated."); // Should be caught by security filter if PreAuthorize is correct
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // The username in UserDetails is the email in our setup
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in DB."));
        return user.getId();
    }


    // GET /api/cart - Get current user's cart items
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Only authenticated users can view their cart
    public ResponseEntity<List<CartItemResponse>> getUserCart() {
        try {
            Long userId = getCurrentUserId(); // Use the helper method
            List<CartItem> cartItems = cartService.getCartByUserId(userId).getCartItems();
            List<CartItemResponse> response = cartItems.stream()
                    .map(CartItemResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) { // Catch if user somehow not authenticated despite @PreAuthorize
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) { // General catch-all for other unexpected errors
            System.err.println("Error fetching user cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // POST /api/cart - Add a product to the cart or update its quantity
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CartItemResponse> addProductToCart(
            @RequestBody AddToCartRequest request) {
        try {
            Long userId = getCurrentUserId(); // Use the helper method
            CartItem updatedItem = cartService.addProductToCart(userId, request.getProductId(), request.getQuantity());
            if (updatedItem == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new CartItemResponse(updatedItem));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            System.err.println("Error adding product to cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // POST /api/cart/remove - Remove product(s) from cart
    @PostMapping("/remove")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> removeProductFromCart(
            @RequestBody AddToCartRequest request) { // Reusing AddToCartRequest for simplicity
        try {
            Long userId = getCurrentUserId(); // Use the helper method
            cartService.removeProductFromCart(userId, request.getProductId(), request.getQuantity());
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.err.println("Error removing product from cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/cart - Clear all items from the cart
    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> clearCart() {
        try {
            Long userId = getCurrentUserId(); // Use the helper method
            cartService.clearCart(userId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}