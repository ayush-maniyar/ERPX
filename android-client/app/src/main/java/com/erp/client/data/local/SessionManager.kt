package com.erp.client.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.erp.client.domain.model.Role

/**
 * Persists the JWT bearer token and signed-in user profile across app restarts,
 * backed by EncryptedSharedPreferences (AES256-GCM values, AES256-SIV keys).
 */
class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSession(token: String, userId: Long, name: String, email: String, role: Role) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROLE, role.name)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getSession(): UserSession? {
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        val userId = prefs.getLong(KEY_USER_ID, -1L)
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val roleName = prefs.getString(KEY_ROLE, null) ?: return null

        return UserSession(
            token = token,
            userId = userId,
            name = name,
            email = email,
            role = Role.valueOf(roleName)
        )
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_FILE_NAME = "erpx_secure_session"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_ROLE = "user_role"
    }
}

data class UserSession(
    val token: String,
    val userId: Long,
    val name: String,
    val email: String,
    val role: Role
)
