package com.erp.client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.client.data.remote.ApiResult
import com.erp.client.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(private val groupRepository: GroupRepository) : ViewModel() {

    private val _createGroupState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val createGroupState: StateFlow<UiState<String>> = _createGroupState

    private val _addStudentState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val addStudentState: StateFlow<UiState<String>> = _addStudentState

    private val _sendEmailState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val sendEmailState: StateFlow<UiState<String>> = _sendEmailState

    fun createGroup(tagName: String, description: String) {
        if (tagName.isBlank()) {
            _createGroupState.value = UiState.Error("Tag name is required")
            return
        }
        viewModelScope.launch {
            _createGroupState.value = UiState.Loading
            when (val result = groupRepository.createGroup(tagName.trim(), description.trim())) {
                is ApiResult.Success -> _createGroupState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _createGroupState.value = UiState.Error(result.message)
            }
        }
    }

    fun addStudentToGroup(tagName: String, studentEmail: String) {
        if (tagName.isBlank() || studentEmail.isBlank()) {
            _addStudentState.value = UiState.Error("Tag name and student email are required")
            return
        }
        viewModelScope.launch {
            _addStudentState.value = UiState.Loading
            when (val result = groupRepository.addStudentToGroup(tagName.trim(), studentEmail.trim())) {
                is ApiResult.Success -> _addStudentState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _addStudentState.value = UiState.Error(result.message)
            }
        }
    }

    fun sendEmailByTag(tagName: String, subject: String, body: String) {
        if (tagName.isBlank() || subject.isBlank() || body.isBlank()) {
            _sendEmailState.value = UiState.Error("Tag, subject, and body are required")
            return
        }
        viewModelScope.launch {
            _sendEmailState.value = UiState.Loading
            when (val result = groupRepository.sendEmailByTag(tagName.trim(), subject.trim(), body)) {
                is ApiResult.Success -> _sendEmailState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _sendEmailState.value = UiState.Error(result.message)
            }
        }
    }

    fun resetCreateGroupState() { _createGroupState.value = UiState.Idle }
    fun resetAddStudentState() { _addStudentState.value = UiState.Idle }
    fun resetSendEmailState() { _sendEmailState.value = UiState.Idle }
}
