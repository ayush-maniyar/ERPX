package com.erp.client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.client.data.remote.ApiResult
import com.erp.client.data.repository.ClassRepository
import com.erp.client.domain.model.VideoClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClassViewModel(private val classRepository: ClassRepository) : ViewModel() {

    private val _createClassState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val createClassState: StateFlow<UiState<String>> = _createClassState

    private val _classListState = MutableStateFlow<UiState<List<VideoClass>>>(UiState.Idle)
    val classListState: StateFlow<UiState<List<VideoClass>>> = _classListState

    fun createVideoClass(title: String, targetTag: String, meetLink: String, scheduledTime: String) {
        if (title.isBlank() || targetTag.isBlank() || meetLink.isBlank() || scheduledTime.isBlank()) {
            _createClassState.value = UiState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _createClassState.value = UiState.Loading
            when (val result = classRepository.createVideoClass(title.trim(), targetTag.trim(), meetLink.trim(), scheduledTime.trim())) {
                is ApiResult.Success -> _createClassState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _createClassState.value = UiState.Error(result.message)
            }
        }
    }

    fun loadClassesByTag(tagName: String) {
        if (tagName.isBlank()) {
            _classListState.value = UiState.Error("A class tag is required")
            return
        }
        viewModelScope.launch {
            _classListState.value = UiState.Loading
            when (val result = classRepository.getClassesByTag(tagName.trim())) {
                is ApiResult.Success -> _classListState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _classListState.value = UiState.Error(result.message)
            }
        }
    }

    fun resetCreateClassState() { _createClassState.value = UiState.Idle }
}
