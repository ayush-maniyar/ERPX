package com.erp.client.data.repository

import com.erp.client.data.local.SessionManager
import com.erp.client.data.local.UserSession
import com.erp.client.data.remote.ApiResult
import com.erp.client.data.remote.ApiService
import com.erp.client.data.remote.dto.LoginRequest
import com.erp.client.data.remote.dto.RegisterRequest
import com.erp.client.data.remote.safeApiCall
import com.erp.client.domain.model.Role

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    suspend fun register(name: String, email: String, password: String, role: Role): ApiResult<String> {
        val result = safeApiCall { apiService.register(RegisterRequest(name, email, password, role)) }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.message)
            is ApiResult.Failure -> result
        }
    }

    suspend fun login(email: String, password: String): ApiResult<UserSession> {
        val result = safeApiCall { apiService.login(LoginRequest(email, password)) }
        return when (result) {
            is ApiResult.Success -> {
                val response = result.data
                sessionManager.saveSession(response.token, response.id, response.name, response.email, response.role)
                ApiResult.Success(
                    UserSession(response.token, response.id, response.name, response.email, response.role)
                )
            }
            is ApiResult.Failure -> result
        }
    }

    fun currentSession(): UserSession? = sessionManager.getSession()

    fun logout() = sessionManager.clearSession()
}
