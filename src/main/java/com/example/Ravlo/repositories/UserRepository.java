package com.example.Ravlo.repositories;

import com.example.Ravlo.enitities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> { //JpaRepository<EntityType, PrimaryKeyType> User is entity class and Long is the type of primary key

    Optional<User> findByEmail(String email);
}

