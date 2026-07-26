package com.erp.client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.client.data.remote.ApiResult
import com.erp.client.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {

    private val _createQuizState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val createQuizState: StateFlow<UiState<String>> = _createQuizState

    private val _submitQuizState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val submitQuizState: StateFlow<UiState<String>> = _submitQuizState

    fun createQuiz(title: String, targetTag: String, questions: List<String>, correctAnswers: List<String>) {
        val cleanQuestions = questions.map { it.trim() }.filter { it.isNotBlank() }
        val cleanAnswers = correctAnswers.map { it.trim() }.filter { it.isNotBlank() }

        if (title.isBlank() || targetTag.isBlank()) {
            _createQuizState.value = UiState.Error("Title and target tag are required")
            return
        }
        if (cleanQuestions.isEmpty() || cleanQuestions.size != cleanAnswers.size) {
            _createQuizState.value = UiState.Error("Every question needs a matching correct answer")
            return
        }

        viewModelScope.launch {
            _createQuizState.value = UiState.Loading
            when (val result = quizRepository.createQuiz(title.trim(), targetTag.trim(), cleanQuestions, cleanAnswers)) {
                is ApiResult.Success -> _createQuizState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _createQuizState.value = UiState.Error(result.message)
            }
        }
    }

    fun submitQuiz(quizId: Long?, studentEmail: String, submittedAnswers: List<String>) {
        if (quizId == null) {
            _submitQuizState.value = UiState.Error("A valid quiz ID is required")
            return
        }
        if (studentEmail.isBlank()) {
            _submitQuizState.value = UiState.Error("Student email is required")
            return
        }

        viewModelScope.launch {
            _submitQuizState.value = UiState.Loading
            when (val result = quizRepository.submitQuiz(quizId, studentEmail.trim(), submittedAnswers)) {
                is ApiResult.Success -> _submitQuizState.value = UiState.Success(result.data)
                is ApiResult.Failure -> _submitQuizState.value = UiState.Error(result.message)
            }
        }
    }

    fun resetCreateQuizState() { _createQuizState.value = UiState.Idle }
    fun resetSubmitQuizState() { _submitQuizState.value = UiState.Idle }
}
