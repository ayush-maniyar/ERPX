package com.erp.client.data.local

import android.content.Context
import android.content.SharedPreferences
import com.erp.client.BuildConfig

/**
 * Stores the backend base URL at runtime so a changed LAN address never
 * requires rebuilding the APK. The compile-time BuildConfig value is only
 * a first-run default; once the user saves an address it wins.
 */
class ServerConfig(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

    /** The active base URL, always normalised to `http://host:port/`. */
    fun baseUrl(): String =
        prefs.getString(KEY_BASE_URL, null) ?: normalise(BuildConfig.API_BASE_URL)

    fun saveBaseUrl(input: String) {
        prefs.edit().putString(KEY_BASE_URL, normalise(input)).apply()
    }

    /** True once the user has explicitly set an address on this device. */
    fun isConfigured(): Boolean = prefs.getString(KEY_BASE_URL, null) != null

    /** Host portion only, for display and for the cleartext permission check. */
    fun host(): String = runCatching {
        java.net.URI(baseUrl()).host.orEmpty()
    }.getOrDefault("")

    companion object {
        private const val PREFS_FILE_NAME = "erpx_server_config"
        private const val KEY_BASE_URL = "base_url"
        const val DEFAULT_PORT = 8080

        /**
         * Accepts what a user actually types — "192.168.1.5", "192.168.1.5:8080",
         * "http://192.168.1.5:8080" — and returns a URL Retrofit will accept.
         */
        fun normalise(input: String): String {
            var value = input.trim()
            if (value.isEmpty()) return value

            if (!value.startsWith("http://") && !value.startsWith("https://")) {
                value = "http://$value"
            }
            // Append the default port when the user gave only a host.
            val afterScheme = value.substringAfter("://")
            val hostAndPort = afterScheme.substringBefore("/")
            if (!hostAndPort.contains(":")) {
                val scheme = value.substringBefore("://")
                val path = afterScheme.substringAfter("/", "")
                value = "$scheme://$hostAndPort:$DEFAULT_PORT" + if (path.isEmpty()) "" else "/$path"
            }
            if (!value.endsWith("/")) value = "$value/"
            return value
        }

        /** Basic sanity check so an obviously wrong entry is caught before saving. */
        fun isPlausible(input: String): Boolean {
            val normalised = normalise(input)
            if (normalised.isEmpty()) return false
            return runCatching {
                val uri = java.net.URI(normalised)
                !uri.host.isNullOrBlank() && uri.port > 0
            }.getOrDefault(false)
        }
    }
}
