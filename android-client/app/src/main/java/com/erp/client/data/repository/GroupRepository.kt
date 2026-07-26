package com.erp.client.data.repository

import com.erp.client.data.remote.ApiResult
import com.erp.client.data.remote.ApiService
import com.erp.client.data.remote.dto.AddStudentToGroupRequest
import com.erp.client.data.remote.dto.CreateGroupRequest
import com.erp.client.data.remote.dto.SendTagEmailRequest
import com.erp.client.data.remote.safeApiCall

class GroupRepository(private val apiService: ApiService) {

    suspend fun createGroup(tagName: String, description: String): ApiResult<String> {
        return safeApiCall { apiService.createGroup(CreateGroupRequest(tagName, description)) }
    }

    suspend fun addStudentToGroup(tagName: String, studentEmail: String): ApiResult<String> {
        return safeApiCall { apiService.addStudentToGroup(AddStudentToGroupRequest(tagName, studentEmail)) }
    }

    suspend fun sendEmailByTag(tagName: String, subject: String, body: String): ApiResult<String> {
        return safeApiCall { apiService.sendEmailByTag(SendTagEmailRequest(tagName, subject, body)) }
    }
}
