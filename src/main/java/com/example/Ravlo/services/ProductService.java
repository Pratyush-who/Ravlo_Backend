package com.example.Ravlo.services;

import com.example.Ravlo.dto.ProductRequest;
import com.example.Ravlo.dto.ProductResponse;
import com.example.Ravlo.entities.Product;
import com.example.Ravlo.entities.Retailer;
import com.example.Ravlo.exception.InvalidCredentialsException;
import com.example.Ravlo.repositories.ProductRepository;
import com.example.Ravlo.repositories.RetailerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RetailerRepository retailerRepository;

    public ProductService(ProductRepository productRepository, RetailerRepository retailerRepository) {
        this.productRepository = productRepository;
        this.retailerRepository = retailerRepository;
    }

    // Get retailer from JWT — never trust frontend for this
    private Retailer getAuthenticatedRetailer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return retailerRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Retailer not found"));
    }

    // Create product — retailer auto-detected from JWT
    public ProductResponse createProduct(ProductRequest request) {
        Retailer retailer = getAuthenticatedRetailer();

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setRetailer(retailer);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    // Get all products for the logged-in retailer
    public List<ProductResponse> getMyProducts() {
        Retailer retailer = getAuthenticatedRetailer();
        return productRepository.findByRetailerId(retailer.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get all products — public, anyone can browse
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get single product by ID — public
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return toResponse(product);
    }

    // Update product — only the owning retailer can update
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Retailer retailer = getAuthenticatedRetailer();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (!product.getRetailer().getId().equals(retailer.getId())) {
            throw new InvalidCredentialsException("You are not authorized to update this product");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return toResponse(productRepository.save(product));
    }

    // Delete product — only the owning retailer can delete
    public void deleteProduct(Long id) {
        Retailer retailer = getAuthenticatedRetailer();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (!product.getRetailer().getId().equals(retailer.getId())) {
            throw new InvalidCredentialsException("You are not authorized to delete this product");
        }

        productRepository.delete(product);
    }

    // Map Product entity to ProductResponse DTO
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getRetailer().getId(),
                product.getRetailer().getName(),
                product.getCreatedAt()
        );
    }
}

