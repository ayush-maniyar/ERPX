package com.erp.client.data.repository

import com.erp.client.data.remote.ApiResult
import com.erp.client.data.remote.ApiService
import com.erp.client.data.remote.dto.AddStudentToGroupRequest
import com.erp.client.data.remote.dto.CreateGroupRequest
import com.erp.client.data.remote.dto.SendTagEmailRequest
import com.erp.client.data.remote.safeApiCall

class GroupRepository(private val apiService: ApiService) {

    suspend fun createGroup(tagName: String, description: String): ApiResult<String> {
        val result = safeApiCall { apiService.createGroup(CreateGroupRequest(tagName, description)) }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.message)
            is ApiResult.Failure -> result
        }
    }

    suspend fun addStudentToGroup(tagName: String, studentEmail: String): ApiResult<String> {
        val result = safeApiCall { apiService.addStudentToGroup(AddStudentToGroupRequest(tagName, studentEmail)) }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.message)
            is ApiResult.Failure -> result
        }
    }

    suspend fun sendEmailByTag(tagName: String, subject: String, body: String): ApiResult<String> {
        val result = safeApiCall { apiService.sendEmailByTag(SendTagEmailRequest(tagName, subject, body)) }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.message)
            is ApiResult.Failure -> result
        }
    }
}
