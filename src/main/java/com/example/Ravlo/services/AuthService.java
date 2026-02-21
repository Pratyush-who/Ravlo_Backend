package com.example.Ravlo.services;

import com.example.Ravlo.dto.AuthResponse;
import com.example.Ravlo.dto.Login;
import com.example.Ravlo.entities.User;
import com.example.Ravlo.exception.EmailAlreadyExistsException;
import com.example.Ravlo.exception.InvalidCredentialsException;
import com.example.Ravlo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public AuthResponse login(Login loginDto) {
        // Find user by email
        User user = userRepository.findByEmail(loginDto.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Compare password (plain text for now)
        if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Return success response
        return new AuthResponse(
            "Login successful",
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}

