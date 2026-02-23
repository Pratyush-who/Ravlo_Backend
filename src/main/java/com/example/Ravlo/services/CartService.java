package com.example.Ravlo.services;

import com.example.Ravlo.dto.customer.cart.AddToCartRequest;
import com.example.Ravlo.dto.customer.cart.CartItemResponse;
import com.example.Ravlo.dto.customer.cart.CartResponse;
import com.example.Ravlo.entities.customer.Cart;
import com.example.Ravlo.entities.customer.CartItem;
import com.example.Ravlo.entities.profiles.Customer;
import com.example.Ravlo.entities.retailer.Product;
import com.example.Ravlo.exception.ResourceNotFoundException;
import com.example.Ravlo.repositories.CartRepository;
import com.example.Ravlo.repositories.CustomerRepository;
import com.example.Ravlo.repositories.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CustomerRepository customerRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    private Customer getAuthenticatedCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private Cart getOrCreateCart(Customer customer) {
        return cartRepository.findCartByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        Customer customer = getAuthenticatedCustomer();
        Cart cart = getOrCreateCart(customer);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        // If product already in cart → just increase quantity
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.getQuantity());
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            cart.getItems().add(item);
        }

        return toResponse(cartRepository.save(cart));
    }

    public CartResponse getMyCart() {
        Customer customer = getAuthenticatedCustomer();
        Cart cart = getOrCreateCart(customer);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long cartItemId, Integer newQuantity) {
        Customer customer = getAuthenticatedCustomer();
        Cart cart = getOrCreateCart(customer);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (newQuantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(newQuantity);
        }

        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeCartItem(Long cartItemId) {
        Customer customer = getAuthenticatedCustomer();
        Cart cart = getOrCreateCart(customer);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cart.getItems().remove(item);
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart() {
        Customer customer = getAuthenticatedCustomer();
        Cart cart = getOrCreateCart(customer);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        return new CartResponse(cart.getId(), itemResponses, total);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getPrice(),
                item.getProduct().getPrice() * item.getQuantity()
        );
    }
}

