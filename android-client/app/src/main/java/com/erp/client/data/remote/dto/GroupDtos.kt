package com.erp.client.data.remote.dto

data class CreateGroupRequest(
    val tagName: String,
    val description: String
)

data class AddStudentToGroupRequest(
    val tagName: String,
    val studentEmail: String
)
