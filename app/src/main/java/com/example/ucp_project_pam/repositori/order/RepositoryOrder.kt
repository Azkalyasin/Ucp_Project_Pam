package com.example.ucp_project_pam.repositori.order

import com.example.ucp_project_pam.apiservice.OrderApiService
import com.example.ucp_project_pam.data.TokenManager
import com.example.ucp_project_pam.modeldata.*
import com.example.ucp_project_pam.repositori.auth.RepositoryAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

interface RepositoryOrder {
    suspend fun createOrder(address: String): Result<Order>
    suspend fun getOrderById(id: Int): Result<Order>

    suspend fun getMyOrders(): Result<List<Order>>
    suspend fun updateOrderStatus(orderNumber: String, status: String): Result<Order>

    suspend fun getAllOrders(): Result<List<Order>>
}

class JaringanRepositoryOrder(
    private val orderApiService: OrderApiService,
    private val tokenManager: TokenManager,
    private val authRepository: RepositoryAuth
) : RepositoryOrder {

    private suspend fun <T> executeWithAuth(apiCall: suspend () -> T): T {
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

    // ==================== CREATE ORDER ====================
    override suspend fun createOrder(address: String): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val request = CreateOrderRequest(address = address)

            val response = executeWithAuth {
                orderApiService.createOrder(request)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal membuat order"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Keranjang kosong atau data tidak valid"
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Keranjang tidak ditemukan"
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

    // ==================== GET ORDER BY ID ====================
    override suspend fun getOrderById(id: Int): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                orderApiService.getOrderById(id)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Order tidak ditemukan"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Order tidak ditemukan"
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

    // ==================== GET MY ORDERS ====================
    override suspend fun getMyOrders(): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                orderApiService.getMyOrders()
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal mengambil daftar order"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
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

    // ==================== UPDATE ORDER STATUS (ADMIN) ====================
    override suspend fun updateOrderStatus(
        orderNumber: String,
        status: String
    ): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateOrderStatusRequest(
                orderNumber = orderNumber,
                status = status
            )

            val response = executeWithAuth {
                orderApiService.updateOrderStatus(request)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal mengupdate status order"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Data tidak valid"
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Order tidak ditemukan"
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

    override suspend fun getAllOrders(): Result<List<Order>> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                orderApiService.getAllOrders()
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal mengambil daftar order"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
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