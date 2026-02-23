package com.example.Ravlo.dto.customer.order;

import com.example.Ravlo.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private OrderStatus status;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}

