package com.example.Ravlo.services;

import com.example.Ravlo.dto.customer.payment.CreatePaymentOrderResponse;
import com.example.Ravlo.dto.customer.payment.VerifyPaymentRequest;
import com.example.Ravlo.entities.customer.Order;
import com.example.Ravlo.entities.profiles.Customer;
import com.example.Ravlo.enums.OrderStatus;
import com.example.Ravlo.exception.ResourceNotFoundException;
import com.example.Ravlo.repositories.CustomerRepository;
import com.example.Ravlo.repositories.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentServices {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public PaymentServices(OrderRepository orderRepository,
                           CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CreatePaymentOrderResponse createRazorpayOrder(Long orderId) throws RazorpayException {

        Customer customer = getAuthenticatedCustomer();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is already " + order.getStatus());
        }

        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (long) (order.getTotalAmount() * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "ravlo_order_" + orderId);

        com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);

        String razorpayOrderId = razorpayOrder.get("id");
        order.setRazorpayOrderId(razorpayOrderId);
        orderRepository.save(order);

        return new CreatePaymentOrderResponse(
                razorpayOrderId,
                (long) (order.getTotalAmount() * 100),
                "INR",
                keyId   // frontend needs this to initialise Razorpay checkout
        );
    }

    @Transactional
    public String verifyPayment(VerifyPaymentRequest request) {

        boolean isValid = verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValid) {
            throw new IllegalArgumentException("Payment verification failed. Signature mismatch.");
        }

        Order order = orderRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found for razorpayOrderId: " + request.getRazorpayOrderId()));

        order.setStatus(OrderStatus.PAID);
        order.setRazorpayPaymentId(request.getRazorpayPaymentId());
        orderRepository.save(order);

        return "Payment verified successfully. Order #" + order.getId() + " is now PAID.";
    }
     private boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String signature) {
        try {
            String data = razorpayOrderId + "|" + razorpayPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private Customer getAuthenticatedCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }
}
