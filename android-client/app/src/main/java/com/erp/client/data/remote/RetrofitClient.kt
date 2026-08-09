package com.erp.client.data.remote

import com.erp.client.BuildConfig
import com.erp.client.data.local.ServerConfig
import com.erp.client.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    fun create(
        sessionManager: SessionManager,
        serverConfig: ServerConfig,
        onUnauthorized: () -> Unit = {}
    ): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            // Order matters: rewrite the host first so retries and auth apply
            // to the address the user currently has configured.
            .addInterceptor(BaseUrlInterceptor(serverConfig))
            .addInterceptor(AuthInterceptor(sessionManager, onUnauthorized))
            .addInterceptor(RetryInterceptor())
            .addInterceptor(loggingInterceptor)
            // Short connect timeout: on a LAN a reachable server answers fast,
            // so a wrong address should fail quickly rather than freeze the UI.
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            // Placeholder only — BaseUrlInterceptor replaces host/port per request.
            .baseUrl(ServerConfig.normalise(BuildConfig.API_BASE_URL))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
