package com.erp.client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.client.data.remote.ApiResult
import com.erp.client.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.erp.client.data.remote.dto.QuizDto

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {

    private val _createQuizState = MutableStateFlow<UiState<QuizDto>>(UiState.Idle)
    val createQuizState: StateFlow<UiState<QuizDto>> = _createQuizState

    private val _teacherQuizzes = MutableStateFlow<UiState<List<QuizDto>>>(UiState.Idle)
    val teacherQuizzes: StateFlow<UiState<List<QuizDto>>> = _teacherQuizzes

    private val _submitQuizState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val submitQuizState: StateFlow<UiState<String>> = _submitQuizState

    private val _availableQuizzes = MutableStateFlow<UiState<List<QuizDto>>>(UiState.Idle)
    val availableQuizzes: StateFlow<UiState<List<QuizDto>>> = _availableQuizzes

    fun createQuiz(
        title: String,
        targetTag: String,
        questions: List<String>,
        correctAnswers: List<String>
    ) {
        val cleanQuestions =
            questions.map { it.trim() }.filter { it.isNotBlank() }

        val cleanAnswers =
            correctAnswers.map { it.trim() }.filter { it.isNotBlank() }

        if (title.isBlank() || targetTag.isBlank()) {
            _createQuizState.value =
                UiState.Error("Title and target tag are required")
            return
        }

        if (cleanQuestions.isEmpty() ||
            cleanQuestions.size != cleanAnswers.size
        ) {
            _createQuizState.value =
                UiState.Error(
                    "Every question needs a matching correct answer"
                )
            return
        }

        viewModelScope.launch {
            _createQuizState.value = UiState.Loading

            when (
                val result = quizRepository.createQuiz(
                    title.trim(),
                    targetTag.trim(),
                    cleanQuestions,
                    cleanAnswers
                )
            ) {
                is ApiResult.Success -> {
                    _createQuizState.value =
                        UiState.Success(result.data)

                    // Refresh the teacher's quiz list
                    loadTeacherQuizzes()
                }

                is ApiResult.Failure -> {
                    _createQuizState.value =
                        UiState.Error(result.message)
                }
            }
        }
    }

    fun loadTeacherQuizzes() {
        viewModelScope.launch {
            _teacherQuizzes.value = UiState.Loading

            when (val result = quizRepository.getMyQuizzes()) {
                is ApiResult.Success -> {
                    _teacherQuizzes.value =
                        UiState.Success(result.data)
                }

                is ApiResult.Failure -> {
                    _teacherQuizzes.value =
                        UiState.Error(result.message)
                }
            }
        }
    }

    fun loadAvailableQuizzes() {
        viewModelScope.launch {
            _availableQuizzes.value = UiState.Loading

            when (val result = quizRepository.getAvailableQuizzes()) {
                is ApiResult.Success -> {
                    _availableQuizzes.value =
                        UiState.Success(result.data)
                }

                is ApiResult.Failure -> {
                    _availableQuizzes.value =
                        UiState.Error(result.message)
                }
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
