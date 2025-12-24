// File: data/TokenManager.kt
package com.example.ucp_project_pam.data

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {

        private const val PREFS_NAME = "auth_prefs"


        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_ROLE = "user_role"
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }


    fun saveUserInfo(
        userId: Int,
        name: String,
        email: String,
        phone: String?,
        role: String
    ) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }


    fun updateAccessToken(accessToken: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }


    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }


    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }


    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }


    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }


    fun getUserPhone(): String? {
        return prefs.getString(KEY_USER_PHONE, null)
    }


    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }


    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}