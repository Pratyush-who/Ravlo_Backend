package com.example.Ravlo.repositories;

import com.example.Ravlo.entities.profiles.Retailer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailerRepository extends JpaRepository<Retailer, Long> {

    Optional<Retailer> findByEmail(String email);
}

