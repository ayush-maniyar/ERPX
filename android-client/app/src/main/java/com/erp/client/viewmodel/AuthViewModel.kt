package com.erp.client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.client.data.local.UserSession
import com.erp.client.data.remote.ApiResult
import com.erp.client.data.repository.AuthRepository
import com.erp.client.domain.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<UserSession>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserSession>> = _loginState

    private val _registerState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val registerState: StateFlow<UiState<String>> = _registerState

    fun currentSession(): UserSession? = authRepository.currentSession()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Email and password are required")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            when (val result = authRepository.login(email.trim(), password)) {
                is ApiResult.Success -> _loginState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _loginState.value = UiState.Error(result.message)
            }
        }
    }

    fun register(name: String, email: String, password: String, role: Role) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = UiState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _registerState.value = UiState.Loading
            when (val result = authRepository.register(name.trim(), email.trim(), password, role)) {
                is ApiResult.Success -> _registerState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _registerState.value = UiState.Error(result.message)
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = UiState.Idle
    }

    fun logout() {
        authRepository.logout()
        _loginState.value = UiState.Idle
    }
}
