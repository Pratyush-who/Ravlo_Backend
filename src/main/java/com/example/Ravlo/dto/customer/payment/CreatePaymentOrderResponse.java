package com.example.Ravlo.dto.customer.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatePaymentOrderResponse {
    private String razorpayOrderId;
    private long amount;
    private String currency;
    private String keyId;
}

