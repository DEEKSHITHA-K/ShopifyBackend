package com.example.backend.service;

import com.example.backend.dto.OrderItemRequest;
import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.Product;
import com.example.backend.model.User;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional // Ensures atomicity of the order creation
    public OrderResponse createOrder(Long userId, OrderRequest orderRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING"); // Initial status

        // Set shipping details
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setShippingCity(orderRequest.getShippingCity());
        order.setShippingPostalCode(orderRequest.getShippingPostalCode());
        order.setShippingCountry(orderRequest.getShippingCountry());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new java.util.ArrayList<>();

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemRequest.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Set the parent order
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtOrder(product.getPrice()); // Capture price at the time of order

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems); // Set bidirectional relationship
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return new OrderResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return orderRepository.findByUserOrderByOrderDateDesc(user).stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    public Optional<OrderResponse> getOrderByIdAndUser(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(userId))
                .map(OrderResponse::new);
    }
}