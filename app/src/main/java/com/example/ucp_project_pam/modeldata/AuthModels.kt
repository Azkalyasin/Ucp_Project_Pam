
package com.example.ucp_project_pam.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(
    @SerialName("email")
    val email: String,

    @SerialName("password")
    val password: String
)

@Serializable
data class RegisterRequest(
    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("phone")
    val phone: String? = null,

    @SerialName("password")
    val password: String,

    @SerialName("confirmPassword")
    val confirmPassword: String
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refreshToken")
    val refreshToken: String
)


@Serializable
data class AuthResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: AuthData
)

@Serializable
data class AuthData(
    @SerialName("user")
    val user: User,

    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String
)


@Serializable
data class RefreshTokenResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: RefreshTokenData
)

@Serializable
data class RefreshTokenData(
    @SerialName("accessToken")
    val accessToken: String
)


@Serializable
data class ProfileResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: ProfileData
)

@Serializable
data class ProfileData(
    @SerialName("user")
    val user: User
)


@Serializable
data class LogoutResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String
)

// User Model
@Serializable
data class User(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("phone")
    val phone: String? = null,

    @SerialName("role")
    val role: String,

    @SerialName("created_at")
    val createdAt: String
)



sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}