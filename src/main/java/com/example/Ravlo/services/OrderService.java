package com.example.Ravlo.services;

import com.example.Ravlo.dto.customer.order.OrderItemRequest;
import com.example.Ravlo.dto.customer.order.OrderItemResponse;
import com.example.Ravlo.dto.customer.order.OrderResponse;
import com.example.Ravlo.dto.customer.order.PlaceOrderRequest;
import com.example.Ravlo.entities.customer.Order;
import com.example.Ravlo.entities.customer.OrderItem;
import com.example.Ravlo.entities.profiles.Customer;
import com.example.Ravlo.entities.retailer.Product;
import com.example.Ravlo.enums.OrderStatus;
import com.example.Ravlo.exception.InsufficientStockException;
import com.example.Ravlo.exception.ResourceNotFoundException;
import com.example.Ravlo.repositories.CustomerRepository;
import com.example.Ravlo.repositories.OrderRepository;
import com.example.Ravlo.repositories.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    private Customer getAuthenticatedCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        Customer customer = getAuthenticatedCustomer();

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Not enough stock for product: " + product.getName()
                        + ". Available: " + product.getStock()
                        + ", Requested: " + itemRequest.getQuantity());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            total += product.getPrice() * itemRequest.getQuantity();

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
            orderItems.add(item);
        }
        order.setItems(orderItems);
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        return toResponse(savedOrder);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public List<OrderResponse> getMyOrders() {
        Customer customer = getAuthenticatedCustomer();
        return orderRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderResponse getOrderById(Long orderId) {
        Customer customer = getAuthenticatedCustomer();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getOrderDate(),
                itemResponses
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceAtPurchase()
        );
    }
}
