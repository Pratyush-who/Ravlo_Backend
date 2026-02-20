package com.example.Ravlo.dto;

import com.example.Ravlo.enitities.Role;

public class RetailerResponse {

    private String message;
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String storeName;
    private String storeAddress;
    private String phoneNumber;

    public RetailerResponse() {
    }

    public RetailerResponse(String message, Long id, String name, String email, Role role, String storeName, String storeAddress, String phoneNumber) {
        this.message = message;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

