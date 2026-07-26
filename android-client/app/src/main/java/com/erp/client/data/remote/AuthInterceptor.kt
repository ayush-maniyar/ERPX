package com.erp.client.data.remote

import com.erp.client.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches the stored JWT bearer token to every outgoing request.
 * Login/register calls carry no token yet, which is harmless — the backend
 * only enforces auth on endpoints that require it.
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

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

        return chain.proceed(request)
    }
}
