package com.example.Ravlo.dto.auth;

import com.example.Ravlo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String token;
}

