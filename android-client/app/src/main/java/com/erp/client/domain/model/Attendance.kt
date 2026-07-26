package com.erp.client.domain.model

data class Attendance(
    val id: Long,
    val student: StudentSummary?,
    val quizTitle: String,
    val status: String,
    val timestamp: String
)

data class StudentSummary(
    val id: Long,
    val name: String,
    val email: String,
    val role: Role
)
