package com.erp.backend.controller;

import com.erp.backend.dto.JwtResponse;
import com.erp.backend.dto.LoginRequest;
import com.erp.backend.dto.RegisterRequest;
import com.erp.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        String response = authService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginRequest request) {
        JwtResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
