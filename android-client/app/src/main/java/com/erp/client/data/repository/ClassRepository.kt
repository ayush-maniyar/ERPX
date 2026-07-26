package com.erp.client.data.repository

import com.erp.client.data.remote.ApiResult
import com.erp.client.data.remote.ApiService
import com.erp.client.data.remote.dto.CreateVideoClassRequest
import com.erp.client.data.remote.safeApiCall
import com.erp.client.domain.model.VideoClass

class ClassRepository(private val apiService: ApiService) {

    suspend fun createVideoClass(
        title: String,
        targetTag: String,
        meetLink: String,
        scheduledTime: String
    ): ApiResult<String> {
        val result = safeApiCall {
            apiService.createVideoClass(CreateVideoClassRequest(title, targetTag, meetLink, scheduledTime))
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.message)
            is ApiResult.Failure -> result
        }
    }

    suspend fun getClassesByTag(tagName: String): ApiResult<List<VideoClass>> {
        val result = safeApiCall { apiService.getClassesByTag(tagName) }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(
                result.data.map {
                    VideoClass(it.id, it.title, it.targetTag, it.meetLink, it.scheduledTime)
                }
            )
            is ApiResult.Failure -> result
        }
    }
}
