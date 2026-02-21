package com.example.Ravlo.services;

import com.example.Ravlo.dto.CustomerResponse;
import com.example.Ravlo.dto.RegisterCustomerRequest;
import com.example.Ravlo.entities.Customer;
import com.example.Ravlo.exception.EmailAlreadyExistsException;
import com.example.Ravlo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final UserRepository userRepository;

    public CustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CustomerResponse createCustomer(RegisterCustomerRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        // Create new customer user
        Customer customer = new Customer(
            request.getName(),
            request.getEmail(),
            request.getPassword(), // TODO: Hash password with BCrypt before production
            request.getPhoneNumber(),
            request.getDeliveryAddress()
        );

        // Save customer
        Customer savedCustomer = userRepository.save(customer);

        // Return response with customer details
        return new CustomerResponse(
            "Customer created successfully",
            savedCustomer.getId(),
            savedCustomer.getName(),
            savedCustomer.getEmail(),
            savedCustomer.getRole(),
            savedCustomer.getPhoneNumber(),
            savedCustomer.getDeliveryAddress()
        );
    }
}

