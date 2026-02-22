package com.example.Ravlo.services;

import com.example.Ravlo.dto.CustomerResponse;
import com.example.Ravlo.dto.RegisterCustomerRequest;
import com.example.Ravlo.entities.Customer;
import com.example.Ravlo.exception.EmailAlreadyExistsException;
import com.example.Ravlo.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public CustomerService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public CustomerResponse createCustomer(RegisterCustomerRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        Customer customer = new Customer(
            request.getName(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getPhoneNumber(),
            request.getDeliveryAddress()
        );

        Customer savedCustomer = userRepository.save(customer);
        String token = jwtService.generateToken(savedCustomer);

        return new CustomerResponse(
            "Customer registered successfully",
            savedCustomer.getId(),
            savedCustomer.getName(),
            savedCustomer.getEmail(),
            savedCustomer.getRole(),
            savedCustomer.getPhoneNumber(),
            savedCustomer.getDeliveryAddress(),
            token
        );
    }
}