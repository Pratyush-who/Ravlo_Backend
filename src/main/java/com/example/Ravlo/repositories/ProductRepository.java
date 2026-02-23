package com.example.Ravlo.repositories;

import com.example.Ravlo.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByRetailerId(Long retailerId);
}

