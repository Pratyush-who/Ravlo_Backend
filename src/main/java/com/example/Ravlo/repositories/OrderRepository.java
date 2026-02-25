package com.example.Ravlo.repositories;

import com.example.Ravlo.entities.customer.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
}