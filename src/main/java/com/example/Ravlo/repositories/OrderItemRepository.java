package com.example.Ravlo.repositories;

import com.example.Ravlo.entities.customer.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
