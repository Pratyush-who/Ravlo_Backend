package com.example.Ravlo.repositories;

import com.example.Ravlo.enitities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}

