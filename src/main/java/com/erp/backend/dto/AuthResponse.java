package com.erp.backend.dto;

import com.erp.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private Long id;
    private String name;
    private String email;
    private Role role;
}