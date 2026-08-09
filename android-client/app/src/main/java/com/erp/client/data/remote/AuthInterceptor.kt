package com.erp.client.data.remote

import com.erp.client.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches the stored JWT bearer token to every outgoing request.
 * Login/register calls carry no token yet, which is harmless — the backend
 * only enforces auth on endpoints that require it.
 *
 * A 401 means the stored token is no longer valid (expired, or signed with a
 * secret the backend no longer uses after a restart). The session is cleared
 * immediately so the app returns to the login screen instead of sitting in a
 * logged-in-but-rejected state where every screen silently fails.
 */
class AuthInterceptor(
    private val sessionManager: SessionManager,
    private val onUnauthorized: () -> Unit = {}
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getToken()
        val original = chain.request()

        val request = if (token != null) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        val response = chain.proceed(request)

        if (response.code == 401 && token != null) {
            sessionManager.clearSession()
            onUnauthorized()
        }

        return response
    }
}
