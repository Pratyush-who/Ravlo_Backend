package com.example.Ravlo.controllers;

import com.example.Ravlo.dto.AuthResponse;
import com.example.Ravlo.dto.CustomerResponse;
import com.example.Ravlo.dto.Login;
import com.example.Ravlo.dto.RegisterCustomerRequest;
import com.example.Ravlo.dto.RegisterRetailerRequest;
import com.example.Ravlo.dto.RetailerResponse;
import com.example.Ravlo.services.AuthService;
import com.example.Ravlo.services.CustomerService;
import com.example.Ravlo.services.RetailerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService;
    private final RetailerService retailerService;

    public AuthController(AuthService authService, CustomerService customerService, RetailerService retailerService) {
        this.authService = authService;
        this.customerService = customerService;
        this.retailerService = retailerService;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<CustomerResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest registerDto) {
        CustomerResponse response = customerService.createCustomer(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register/retailer")
    public ResponseEntity<RetailerResponse> registerRetailer(@Valid @RequestBody RegisterRetailerRequest registerDto) {
        RetailerResponse response = retailerService.createRetailer(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody Login loginDto) {
        AuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}
