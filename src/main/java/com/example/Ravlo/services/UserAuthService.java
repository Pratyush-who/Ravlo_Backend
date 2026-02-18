package com.example.Ravlo.services;

import com.example.Ravlo.dto.AuthResponse;
import com.example.Ravlo.dto.Login;
import com.example.Ravlo.dto.Register;
import com.example.Ravlo.enitities.User;
import com.example.Ravlo.exception.EmailAlreadyExistsException;
import com.example.Ravlo.exception.InvalidCredentialsException;
import com.example.Ravlo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

    private final UserRepository userRepository;

    public UserAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(Register registerDto) {
        // Check if email already exists
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered: " + registerDto.getEmail());
        }

        // Create new user
        User user = new User(
            registerDto.getName(),
            registerDto.getEmail(),
            registerDto.getPassword(), // TODO: Hash password with BCrypt before production
            registerDto.getRole()
        );

        // Save user
        User savedUser = userRepository.save(user);

        // Return success response
        return new AuthResponse(
            "Registration successful",
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
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
