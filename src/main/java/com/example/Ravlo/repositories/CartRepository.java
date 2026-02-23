package com.example.Ravlo.repositories;

import com.example.Ravlo.entities.customer.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findCartByCustomerId(Long customerId);
}
