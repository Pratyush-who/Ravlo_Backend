package com.example.Ravlo.controllers;

import com.example.Ravlo.dto.customer.payment.CreatePaymentOrderResponse;
import com.example.Ravlo.dto.customer.payment.VerifyPaymentRequest;
import com.example.Ravlo.services.PaymentServices;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@PreAuthorize("hasRole('CUSTOMER')")
public class PaymentController {

    private final PaymentServices paymentServices;

    public PaymentController(PaymentServices paymentServices) {
        this.paymentServices = paymentServices;
    }

    @PostMapping("/create-order")
    public ResponseEntity<CreatePaymentOrderResponse> createPaymentOrder(
            @RequestParam Long orderId) throws RazorpayException {
        CreatePaymentOrderResponse response = paymentServices.createRazorpayOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {
        String message = paymentServices.verifyPayment(request);
        return ResponseEntity.ok(message);
    }
}

