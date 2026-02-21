package com.example.Ravlo.dto;

import com.example.Ravlo.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private String message;
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String phoneNumber;
    private String deliveryAddress;
}

