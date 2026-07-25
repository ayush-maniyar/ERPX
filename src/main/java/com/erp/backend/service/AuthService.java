package com.erp.backend.service;

import com.erp.backend.dto.AuthResponse;
import com.erp.backend.dto.LoginRequest;
import com.erp.backend.dto.RegisterRequest;
import com.erp.backend.model.User;
import com.erp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        return "User registered successfully!";
    }

    public AuthResponse loginUser(LoginRequest request) {
        // 1. Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Invalid email or password!"));

        // 2. Verify password matches BCrypt hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Error: Invalid email or password!");
        }

        // 3. Return user profile
        return new AuthResponse("Login successful!", user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}