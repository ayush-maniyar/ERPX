package com.erp.client.data

import android.content.Context
import com.erp.client.data.local.SessionManager
import com.erp.client.data.remote.ApiService
import com.erp.client.data.remote.RetrofitClient
import com.erp.client.data.repository.AuthRepository
import com.erp.client.data.repository.ClassRepository
import com.erp.client.data.repository.GroupRepository
import com.erp.client.data.repository.QuizRepository

/**
 * Minimal manual dependency container — avoids pulling in a DI framework
 * for an app this size while keeping ViewModels testable via constructor injection.
 */
class AppContainer(context: Context) {

    val sessionManager: SessionManager = SessionManager(context.applicationContext)

    private val apiService: ApiService = RetrofitClient.create(sessionManager)

    val authRepository = AuthRepository(apiService, sessionManager)
    val groupRepository = GroupRepository(apiService)
    val quizRepository = QuizRepository(apiService)
    val classRepository = ClassRepository(apiService)
}
