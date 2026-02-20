package com.example.Ravlo.services;

import com.example.Ravlo.dto.CreateRetailerRequest;
import com.example.Ravlo.dto.RetailerResponse;
import com.example.Ravlo.enitities.Role;
import com.example.Ravlo.enitities.User;
import com.example.Ravlo.exception.EmailAlreadyExistsException;
import com.example.Ravlo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RetailerService {

    private final UserRepository userRepository;

    public RetailerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RetailerResponse createRetailer(CreateRetailerRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        // Create new retailer user
        User retailer = new User(
            request.getName(),
            request.getEmail(),
            request.getPassword(), // TODO: Hash password with BCrypt before production
            Role.RETAILER
        );

        // Save retailer
        User savedRetailer = userRepository.save(retailer);

        // Return response with retailer details
        return new RetailerResponse(
            "Retailer created successfully",
            savedRetailer.getId(),
            savedRetailer.getName(),
            savedRetailer.getEmail(),
            savedRetailer.getRole(),
            request.getStoreName(),
            request.getStoreAddress(),
            request.getPhoneNumber()
        );
    }
}

