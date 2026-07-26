package com.erp.client.data.repository

import com.erp.client.data.remote.ApiResult
import com.erp.client.data.remote.ApiService
import com.erp.client.data.remote.dto.CreateQuizRequest
import com.erp.client.data.remote.dto.SubmitQuizRequest
import com.erp.client.data.remote.safeApiCall
import com.erp.client.domain.model.Attendance
import com.erp.client.domain.model.Role
import com.erp.client.domain.model.StudentSummary

class QuizRepository(private val apiService: ApiService) {

    suspend fun createQuiz(
        title: String,
        targetTag: String,
        questions: List<String>,
        correctAnswers: List<String>
    ): ApiResult<String> {
        val result = safeApiCall {
            apiService.createQuiz(CreateQuizRequest(title, targetTag, questions, correctAnswers))
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.message)
            is ApiResult.Failure -> result
        }
    }

    suspend fun submitQuiz(
        quizId: Long,
        studentEmail: String,
        submittedAnswers: List<String>
    ): ApiResult<String> {
        val result = safeApiCall {
            apiService.submitQuiz(SubmitQuizRequest(quizId, studentEmail, submittedAnswers))
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.message)
            is ApiResult.Failure -> result
        }
    }

    suspend fun getAttendance(email: String): ApiResult<List<Attendance>> {
        val result = safeApiCall { apiService.getAttendance(email) }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(
                result.data.map { dto ->
                    Attendance(
                        id = dto.id,
                        student = dto.student?.let {
                            StudentSummary(it.id, it.name, it.email, Role.valueOf(it.role))
                        },
                        quizTitle = dto.quizTitle,
                        status = dto.status,
                        timestamp = dto.timestamp
                    )
                }
            )
            is ApiResult.Failure -> result
        }
    }
}
