package com.example.ucp_project_pam.apiservice

import com.example.ucp_project_pam.modeldata.AuthResponse
import com.example.ucp_project_pam.modeldata.LoginRequest
import com.example.ucp_project_pam.modeldata.LogoutResponse
import com.example.ucp_project_pam.modeldata.ProfileResponse
import com.example.ucp_project_pam.modeldata.RefreshTokenRequest
import com.example.ucp_project_pam.modeldata.RefreshTokenResponse
import com.example.ucp_project_pam.modeldata.RegisterRequest
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header


interface AuthApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @GET("me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>
}