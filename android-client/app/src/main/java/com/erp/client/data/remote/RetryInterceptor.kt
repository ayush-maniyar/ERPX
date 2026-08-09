package com.erp.client.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Retries a request when the connection itself fails — the common case when
 * Wi-Fi has just reconnected, the device woke from sleep, or the backend was
 * restarting. Uses a short backoff so a genuinely unreachable server still
 * surfaces an error promptly rather than hanging for the full timeout budget.
 */
class RetryInterceptor(
    private val maxAttempts: Int = 3,
    private val initialBackoffMs: Long = 500L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastFailure: IOException? = null
        var backoff = initialBackoffMs

        repeat(maxAttempts) { attempt ->
            try {
                val response = chain.proceed(request)
                // 5xx during a backend restart is worth one more try; 4xx is not.
                if (response.code in 500..599 && attempt < maxAttempts - 1) {
                    response.close()
                    Thread.sleep(backoff)
                    backoff *= 2
                    return@repeat
                }
                return response
            } catch (e: IOException) {
                lastFailure = e
                if (attempt < maxAttempts - 1) {
                    try {
                        Thread.sleep(backoff)
                    } catch (interrupted: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw IOException("Request interrupted", interrupted)
                    }
                    backoff *= 2
                }
            }
        }

        throw lastFailure ?: IOException("Request failed after $maxAttempts attempts")
    }
}
