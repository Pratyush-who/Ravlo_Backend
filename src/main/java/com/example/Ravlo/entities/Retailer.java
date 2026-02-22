package com.example.Ravlo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "retailers")
public class Retailer extends User {

    @NotBlank(message = "Business name is required")
    @Column(nullable = false)
    private String businessName;

    private String businessAddress;

    private String phoneNumber;

    public Retailer(String name, String email, String password, String businessName, String businessAddress, String phoneNumber) {
        super(name, email, password, Role.RETAILER);
        this.businessName = businessName;
        this.businessAddress = businessAddress;
        this.phoneNumber = phoneNumber;
    }
}
