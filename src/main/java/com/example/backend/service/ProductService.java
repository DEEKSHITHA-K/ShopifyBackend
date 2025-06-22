package com.example.backend.service;

import com.example.backend.dto.ProductResponse;
import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new) // Convert Product entity to ProductResponse DTO
                .collect(Collectors.toList());
    }

    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::new); // Convert to DTO
    }

    public ProductResponse createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);
    }

    public ProductResponse updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
            .map(product -> {
                product.setName(productDetails.getName());
                product.setDescription(productDetails.getDescription());
                product.setPrice(productDetails.getPrice());
                product.setImageUrl(productDetails.getImageUrl());
                Product updatedProduct = productRepository.save(product);
                return new ProductResponse(updatedProduct);
            })
            .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }
}