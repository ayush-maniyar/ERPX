package com.erp.backend.dto;

import com.erp.backend.model.Role;
import lombok.Data;

@Data
public class JwtResponse {
    private String message;
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String name;
    private String email;
    private Role role;

    public JwtResponse(String message, String token, Long id, String name, String email, Role role) {
        this.message = message;
        this.token = token;
        this.tokenType = "Bearer";
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
