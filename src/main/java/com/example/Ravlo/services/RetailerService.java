package com.example.Ravlo.services;

import com.example.Ravlo.dto.auth.RegisterRetailerRequest;
import com.example.Ravlo.dto.auth.RetailerResponse;
import com.example.Ravlo.entities.profiles.Retailer;
import com.example.Ravlo.exception.EmailAlreadyExistsException;
import com.example.Ravlo.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RetailerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RetailerService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RetailerResponse createRetailer(RegisterRetailerRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        Retailer retailer = new Retailer(
            request.getName(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getBusinessName(),
            request.getBusinessAddress(),
            request.getPhoneNumber()
        );

        Retailer savedRetailer = userRepository.save(retailer);
        String token = jwtService.generateToken(savedRetailer);

        return new RetailerResponse(
            "Retailer registered successfully",
            savedRetailer.getId(),
            savedRetailer.getName(),
            savedRetailer.getEmail(),
            savedRetailer.getRole(),
            savedRetailer.getBusinessName(),
            savedRetailer.getBusinessAddress(),
            savedRetailer.getPhoneNumber(),
            token
        );
    }
}
