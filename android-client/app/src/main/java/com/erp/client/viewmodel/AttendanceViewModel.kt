package com.erp.client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.client.data.remote.ApiResult
import com.erp.client.data.repository.AuthRepository
import com.erp.client.data.repository.QuizRepository
import com.erp.client.domain.model.Attendance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val quizRepository: QuizRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _attendanceState = MutableStateFlow<UiState<List<Attendance>>>(UiState.Idle)
    val attendanceState: StateFlow<UiState<List<Attendance>>> = _attendanceState

    fun loadAttendanceForCurrentUser() {
        val email = authRepository.currentSession()?.email
        if (email == null) {
            _attendanceState.value = UiState.Error("No signed-in user session found")
            return
        }
        loadAttendance(email)
    }

    fun loadAttendance(email: String) {
        viewModelScope.launch {
            _attendanceState.value = UiState.Loading
            when (val result = quizRepository.getAttendance(email)) {
                is ApiResult.Success -> _attendanceState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _attendanceState.value = UiState.Error(result.message)
            }
        }
    }
}
