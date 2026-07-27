package com.erp.client.data.remote.dto

data class StudentDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)

data class AttendanceDto(
    val id: Long,
    val student: StudentDto?,
    val quizTitle: String,
    val status: String,
    val timestamp: String
)

data class VideoClassDto(
    val id: Long,
    val title: String,
    val targetTag: String,
    val meetLink: String,
    val scheduledTime: String
)
