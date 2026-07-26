package com.erp.client.data.remote.dto

data class CreateQuizRequest(
    val title: String,
    val targetTag: String,
    val questions: List<String>,
    val correctAnswers: List<String>
)

data class SubmitQuizRequest(
    val quizId: Long,
    val studentEmail: String,
    val submittedAnswers: List<String>
)
