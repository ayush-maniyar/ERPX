package com.erp.client.data.remote.dto

import com.erp.client.domain.model.Role

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: Role
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class JwtResponse(
    val message: String,
    val token: String,
    val tokenType: String,
    val id: Long,
    val name: String,
    val email: String,
    val role: Role
)
