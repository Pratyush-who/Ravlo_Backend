package com.example.Ravlo.entities.profiles;

import com.example.Ravlo.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends User {

    private String phoneNumber;

    private String deliveryAddress;

    public Customer(String name, String email, String password, String phoneNumber, String deliveryAddress) {
        super(name, email, password, Role.CUSTOMER);
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
    }
}
