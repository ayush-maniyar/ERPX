package com.erp.client.data

import android.content.Context
import com.erp.client.data.local.ServerConfig
import com.erp.client.data.local.SessionManager
import com.erp.client.data.remote.ApiService
import com.erp.client.data.remote.RetrofitClient
import com.erp.client.data.repository.AuthRepository
import com.erp.client.data.repository.ClassRepository
import com.erp.client.data.repository.GroupRepository
import com.erp.client.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Minimal manual dependency container — avoids pulling in a DI framework
 * for an app this size while keeping ViewModels testable via constructor injection.
 */
class AppContainer(context: Context) {

    val sessionManager: SessionManager = SessionManager(context.applicationContext)
    val serverConfig: ServerConfig = ServerConfig(context.applicationContext)

    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Emits when the backend rejects the stored token, so the UI can return to login. */
    val sessionExpired: SharedFlow<Unit> = _sessionExpired

    private val apiService: ApiService = RetrofitClient.create(
        sessionManager = sessionManager,
        serverConfig = serverConfig,
        onUnauthorized = { _sessionExpired.tryEmit(Unit) }
    )

    val authRepository = AuthRepository(apiService, sessionManager)
    val groupRepository = GroupRepository(apiService)
    val quizRepository = QuizRepository(apiService)
    val classRepository = ClassRepository(apiService)
}
