package com.example.Ravlo.controllers;

import com.example.Ravlo.dto.AuthResponse;
import com.example.Ravlo.dto.Login;
import com.example.Ravlo.dto.Register;
import com.example.Ravlo.services.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody Register registerDto) {
        AuthResponse response = userAuthService.register(registerDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody Login loginDto) {
        AuthResponse response = userAuthService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}

