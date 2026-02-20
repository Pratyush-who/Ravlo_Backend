package com.example.Ravlo.controllers;

import com.example.Ravlo.dto.CreateRetailerRequest;
import com.example.Ravlo.dto.RetailerResponse;
import com.example.Ravlo.services.RetailerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/retailers")
public class RetailerController {

    private final RetailerService retailerService;

    public RetailerController(RetailerService retailerService) {
        this.retailerService = retailerService;
    }

    @PostMapping
    public ResponseEntity<RetailerResponse> createRetailer(@Valid @RequestBody CreateRetailerRequest request) {
        RetailerResponse response = retailerService.createRetailer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

