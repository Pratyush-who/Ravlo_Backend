package com.example.Ravlo.services;

import com.example.Ravlo.dto.AuthResponse;
import com.example.Ravlo.dto.Login;
import com.example.Ravlo.entities.User;
import com.example.Ravlo.exception.InvalidCredentialsException;
import com.example.Ravlo.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(Login loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
            "Login successful",
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            token
        );
    }
}
