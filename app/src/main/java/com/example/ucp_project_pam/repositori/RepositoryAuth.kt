
package com.example.ucp_project_pam.repositori

import com.example.ucp_project_pam.apiservice.AuthApiService
import com.example.ucp_project_pam.data.TokenManager
import com.example.ucp_project_pam.modeldata.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

interface RepositoryAuth {
    suspend fun login(loginRequest: LoginRequest): Result<AuthResponse>
    suspend fun register(registerRequest: RegisterRequest): Result<AuthResponse>
    suspend fun refreshToken(): Result<String>
    suspend fun getProfile(): Result<User>
    suspend fun logout(): Result<Unit>
    fun isLoggedIn(): Boolean
}

class JaringanRepositoryAuth(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : RepositoryAuth {

    // ==================== LOGIN ====================
    override suspend fun login(
        loginRequest: LoginRequest
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApiService.login(loginRequest)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    tokenManager.saveTokens(
                        body.data.accessToken,
                        body.data.refreshToken
                    )
                    tokenManager.saveUserInfo(
                        userId = body.data.user.id,
                        name = body.data.user.name,
                        email = body.data.user.email,
                        phone = body.data.user.phone,
                        role = body.data.user.role
                    )
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.message ?: "Login gagal"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Email atau password salah"
                    404 -> "Endpoint tidak ditemukan"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan: ${e.message}"))
        }
    }

    // ==================== REGISTER ====================
    override suspend fun register(
        registerRequest: RegisterRequest
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApiService.register(registerRequest)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    // Simpan tokens dan user info
                    tokenManager.saveTokens(
                        body.data.accessToken,
                        body.data.refreshToken
                    )
                    tokenManager.saveUserInfo(
                        userId = body.data.user.id,
                        name = body.data.user.name,
                        email = body.data.user.email,
                        phone = body.data.user.phone,
                        role = body.data.user.role
                    )
                    Result.success(body)
                } else {
                    Result.failure(Exception(body?.message ?: "Registrasi gagal"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    409 -> "Email sudah terdaftar"
                    400 -> "Data tidak valid"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan: ${e.message}"))
        }
    }

    // ==================== REFRESH TOKEN ====================
    override suspend fun refreshToken(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val refreshToken = tokenManager.getRefreshToken()
                ?: return@withContext Result.failure(Exception("Refresh token tidak ditemukan"))

            val request = RefreshTokenRequest(refreshToken = refreshToken)
            val response = authApiService.refreshToken(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    // Update access token
                    tokenManager.updateAccessToken(body.data.accessToken)
                    Result.success(body.data.accessToken)
                } else {
                    Result.failure(Exception(body?.message ?: "Refresh token gagal"))
                }
            } else {
                // Token expired, harus login ulang
                tokenManager.clearAll()
                Result.failure(Exception("Sesi expired, silakan login kembali"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan: ${e.message}"))
        }
    }

    // ==================== GET PROFILE ====================
    override suspend fun getProfile(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val token = tokenManager.getAccessToken()
                ?: return@withContext Result.failure(Exception("Token tidak ditemukan"))

            val response = authApiService.getProfile("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    // Update user info di local storage
                    tokenManager.saveUserInfo(
                        userId = body.data.user.id,
                        name = body.data.user.name,
                        email = body.data.user.email,
                        phone = body.data.user.phone,
                        role = body.data.user.role
                    )
                    Result.success(body.data.user)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal mengambil profil"))
                }
            } else if (response.code() == 401) {
                // Token expired, coba refresh
                val refreshResult = refreshToken()
                if (refreshResult.isSuccess) {
                    // Retry get profile dengan token baru
                    return@withContext getProfile()
                } else {
                    Result.failure(Exception("Sesi expired, silakan login kembali"))
                }
            } else {
                Result.failure(Exception("Gagal mengambil profil"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan: ${e.message}"))
        }
    }

    // ==================== LOGOUT ====================
    override suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = tokenManager.getAccessToken()
            if (token != null) {
                // Panggil API logout
                val response = authApiService.logout("Bearer $token")
                // Tidak peduli berhasil atau tidak, tetap hapus data lokal
            }

            // Hapus semua data lokal
            tokenManager.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            // Tetap hapus data lokal meskipun API call gagal
            tokenManager.clearAll()
            Result.success(Unit)
        }
    }

    // ==================== CHECK LOGIN STATUS ====================
    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
}