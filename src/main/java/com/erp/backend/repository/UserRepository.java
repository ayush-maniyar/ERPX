package com.erp.backend.repository;

import com.erp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query method: Spring Boot automatically generates the SQL to find a user by email!
    Optional<User> findByEmail(String email);

    // Checks if an email already exists in the database
    Boolean existsByEmail(String email);
}