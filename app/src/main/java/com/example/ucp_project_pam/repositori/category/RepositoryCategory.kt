package com.example.ucp_project_pam.repositori.category

import com.example.ucp_project_pam.apiservice.CategoryApiService
import com.example.ucp_project_pam.data.TokenManager
import com.example.ucp_project_pam.modeldata.*
import com.example.ucp_project_pam.repositori.auth.RepositoryAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

interface RepositoryCategory {
    suspend fun getAllCategories(): Result<List<Category>>
    suspend fun getCategoryById(id: Int): Result<Category>
    suspend fun createCategory(request: CreateCategoryRequest): Result<Category>
    suspend fun updateCategory(id: Int, request: UpdateCategoryRequest): Result<Category>
    suspend fun deleteCategory(id: Int): Result<Category>
}

class JaringanRepositoryCategory(
    private val categoryApiService: CategoryApiService,
    private val tokenManager: TokenManager,
    private val authRepository: RepositoryAuth
) : RepositoryCategory {

    private suspend fun <T> executeWithAuth(
        apiCall: suspend () -> T
    ): T {
        return try {
            if (!tokenManager.isLoggedIn()) {
                throw Exception("Silakan login terlebih dahulu")
            }
            apiCall()
        } catch (e: Exception) {
            if (e.message?.contains("401") == true ||
                e.message?.contains("Unauthorized") == true) {
                val refreshResult = authRepository.refreshToken()
                if (refreshResult.isSuccess) {
                    apiCall()
                } else {
                    throw Exception("Sesi expired, silakan login kembali")
                }
            } else {
                throw e
            }
        }
    }

    override suspend fun getAllCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                categoryApiService.getCategories()
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Gagal mengambil data kategori"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Endpoint tidak ditemukan"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    override suspend fun getCategoryById(id: Int): Result<Category> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                categoryApiService.getCategoryById(id)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Kategori tidak ditemukan"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Kategori tidak ditemukan"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }


    override suspend fun createCategory(
        request: CreateCategoryRequest
    ): Result<Category> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                categoryApiService.createCategory(request)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal membuat kategori"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Data tidak valid"
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    409 -> "Kategori sudah ada"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    override suspend fun updateCategory(
        id: Int,
        request: UpdateCategoryRequest
    ): Result<Category> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                categoryApiService.updateCategory(id, request)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal mengupdate kategori"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Data tidak valid"
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Kategori tidak ditemukan"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }


    override suspend fun deleteCategory(id: Int): Result<Category> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                categoryApiService.deleteCategory(id)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal menghapus kategori"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Kategori tidak ditemukan"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }
}