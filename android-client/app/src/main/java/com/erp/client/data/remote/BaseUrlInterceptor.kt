package com.erp.client.data.remote

import com.erp.client.data.local.ServerConfig
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Retrofit fixes its base URL at construction time, so a URL the user edits
 * later would otherwise be ignored until the process restarts. This rewrites
 * the scheme/host/port of every outgoing request from [ServerConfig], making
 * an address change take effect on the very next call.
 */
class BaseUrlInterceptor(private val serverConfig: ServerConfig) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val configured = serverConfig.baseUrl().toHttpUrlOrNull()
            ?: return chain.proceed(request)

        val rewritten = request.url.newBuilder()
            .scheme(configured.scheme)
            .host(configured.host)
            .port(configured.port)
            .build()

        return chain.proceed(request.newBuilder().url(rewritten).build())
    }
}
