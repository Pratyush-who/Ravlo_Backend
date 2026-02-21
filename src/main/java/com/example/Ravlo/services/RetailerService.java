package com.example.Ravlo.services;

import com.example.Ravlo.dto.RegisterRetailerRequest;
import com.example.Ravlo.dto.RetailerResponse;
import com.example.Ravlo.entities.Role;
import com.example.Ravlo.entities.Retailer;
import com.example.Ravlo.exception.EmailAlreadyExistsException;
import com.example.Ravlo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RetailerService {

    private final UserRepository userRepository;

    public RetailerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RetailerResponse createRetailer(RegisterRetailerRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        // Create new retailer user
        Retailer retailer = new Retailer(
            request.getName(),
            request.getEmail(),
            request.getPassword(), // TODO: Hash password with BCrypt before production
            request.getBusinessName(),
            request.getBusinessAddress(),
            request.getPhoneNumber()
        );

        // Save retailer
        Retailer savedRetailer = userRepository.save(retailer);

        // Return response with retailer details
        return new RetailerResponse(
            "Retailer created successfully",
            savedRetailer.getId(),
            savedRetailer.getName(),
            savedRetailer.getEmail(),
            savedRetailer.getRole(),
            savedRetailer.getBusinessName(),
            savedRetailer.getBusinessAddress(),
            savedRetailer.getPhoneNumber()
        );
    }
}

