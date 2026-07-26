package com.erp.client.data.remote

import com.erp.client.data.remote.dto.AddStudentToGroupRequest
import com.erp.client.data.remote.dto.AttendanceDto
import com.erp.client.data.remote.dto.CreateGroupRequest
import com.erp.client.data.remote.dto.CreateQuizRequest
import com.erp.client.data.remote.dto.CreateVideoClassRequest
import com.erp.client.data.remote.dto.JwtResponse
import com.erp.client.data.remote.dto.LoginRequest
import com.erp.client.data.remote.dto.RegisterRequest
import com.erp.client.data.remote.dto.SendTagEmailRequest
import com.erp.client.data.remote.dto.SubmitQuizRequest
import com.erp.client.data.remote.dto.VideoClassDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<JwtResponse>

    @POST("api/groups/create")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<String>

    @POST("api/groups/add-student")
    suspend fun addStudentToGroup(@Body request: AddStudentToGroupRequest): Response<String>

    @POST("api/email/send-by-tag")
    suspend fun sendEmailByTag(@Body request: SendTagEmailRequest): Response<String>

    @POST("api/quiz/create")
    suspend fun createQuiz(@Body request: CreateQuizRequest): Response<String>

    @POST("api/quiz/submit")
    suspend fun submitQuiz(@Body request: SubmitQuizRequest): Response<String>

    @GET("api/quiz/attendance/{email}")
    suspend fun getAttendance(@Path("email") email: String): Response<List<AttendanceDto>>

    @POST("api/classes/create")
    suspend fun createVideoClass(@Body request: CreateVideoClassRequest): Response<String>

    @GET("api/classes/tag/{tagName}")
    suspend fun getClassesByTag(@Path("tagName") tagName: String): Response<List<VideoClassDto>>
}
