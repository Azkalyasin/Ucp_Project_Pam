package com.example.ucp_project_pam.repositori.menu

import android.content.Context
import android.net.Uri
import com.example.ucp_project_pam.apiservice.MenuApiService
import com.example.ucp_project_pam.data.TokenManager
import com.example.ucp_project_pam.modeldata.*
import com.example.ucp_project_pam.repositori.auth.RepositoryAuth
import com.example.ucp_project_pam.viewmodel.menu.MenuFormState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

interface RepositoryMenu {
    suspend fun getAllMenus(
        categoryId: Int? = null,
        isAvailable: Boolean? = null,
        search: String? = null
    ): Result<List<Menu>>

    suspend fun getMenuById(id: Int): Result<Menu>
    suspend fun createMenu(formState: MenuFormState, context: Context): Result<Menu>
    suspend fun updateMenu(formState: MenuFormState, context: Context): Result<Menu>
    suspend fun deleteMenu(id: Int): Result<Menu>
}

class JaringanRepositoryMenu(
    private val menuApiService: MenuApiService,
    private val tokenManager: TokenManager,
    private val authRepository: RepositoryAuth
) : RepositoryMenu {

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

    // ==================== GET ALL MENUS ====================
    override suspend fun getAllMenus(
        categoryId: Int?,
        isAvailable: Boolean?,
        search: String?
    ): Result<List<Menu>> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                menuApiService.getMenus(categoryId, isAvailable, search)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Gagal mengambil data menu"))
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

    // ==================== GET MENU BY ID ====================
    override suspend fun getMenuById(id: Int): Result<Menu> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                menuApiService.getMenuById(id)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Menu tidak ditemukan"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Menu tidak ditemukan"
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

    // ==================== CREATE MENU ====================
    override suspend fun createMenu(
        formState: MenuFormState,
        context: Context
    ): Result<Menu> = withContext(Dispatchers.IO) {
        try {
            val namePart = formState.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = formState.description.takeIf { it.isNotBlank() }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = formState.price.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryPart = formState.categoryId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val stockPart = formState.stock.takeIf { it.isNotBlank() }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
            val isAvailablePart = formState.isAvailable.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = formState.imageUri?.let { uriString ->
                prepareImagePart(Uri.parse(uriString), context)
            }

            val response = executeWithAuth {
                menuApiService.createMenu(
                    name = namePart,
                    description = descriptionPart,
                    price = pricePart,
                    categoryId = categoryPart,
                    isAvailable = isAvailablePart,
                    stock = stockPart,
                    image = imagePart
                )
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal menambahkan menu"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Data tidak valid"
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

    // ==================== UPDATE MENU ====================
    override suspend fun updateMenu(
        formState: MenuFormState,
        context: Context
    ): Result<Menu> = withContext(Dispatchers.IO) {
        try {
            // ===== DEBUG: CEK DATA YANG DIKIRIM =====
            println("=================================")
            println("REPOSITORY - UPDATE MENU:")
            println("Sending to API...")
            println("Menu ID: ${formState.id}")
            println("Name: ${formState.name}")
            println("Price: ${formState.price}")
            println("CategoryId: ${formState.categoryId}")
            println("Stock: ${formState.stock}")
            println("IsAvailable: ${formState.isAvailable}")
            println("ImageUri: ${formState.imageUri}")

            // CEK APAKAH ID VALID
            if (formState.id <= 0) {
                println("ERROR: ID tidak valid! ID = ${formState.id}")
                return@withContext Result.failure(Exception("ID Menu tidak valid"))
            }

            println("=================================")

            val namePart = formState.name.takeIf { it.isNotBlank() }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            val descriptionPart = formState.description.takeIf { it.isNotBlank() }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            val pricePart = formState.price.takeIf { it.isNotBlank() }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            val categoryPart = if (formState.categoryId > 0) {
                formState.categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            } else {
                println("WARNING: CategoryId = 0, akan dikirim sebagai null")
                null
            }

            val stockPart = formState.stock.takeIf { it.isNotBlank() }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            val isAvailablePart = formState.isAvailable.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = formState.imageUri?.let { uriString ->
                if (uriString.startsWith("http")) {
                    println("Image: Skip (already uploaded)")
                    null
                } else {
                    println("Image: Will upload new image")
                    prepareImagePart(Uri.parse(uriString), context)
                }
            }

            println("Calling API: PATCH /menus/${formState.id}")

            val response = executeWithAuth {
                menuApiService.updateMenu(
                    id = formState.id,
                    name = namePart,
                    description = descriptionPart,
                    price = pricePart,
                    categoryId = categoryPart,
                    isAvailable = isAvailablePart,
                    stock = stockPart,
                    image = imagePart
                )
            }

            println("API Response Code: ${response.code()}")
            println("API Response Success: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val body = response.body()
                println("Response Body Success: ${body?.success}")
                println("Response Body Message: ${body?.message}")
                println("Response Body Data: ${body?.data}")

                if (body != null && body.success && body.data != null) {
                    println("SUCCESS: Menu updated!")
                    Result.success(body.data)
                } else {
                    println("ERROR: Response body invalid")
                    Result.failure(Exception(body?.message ?: "Gagal mengupdate menu"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                println("ERROR Response Body: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> "Data tidak valid"
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Menu tidak ditemukan"
                    500 -> "Server error, coba lagi nanti"
                    else -> "Error: ${response.code()}"
                }
                println("ERROR: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            println("ERROR IOException: ${e.message}")
            Result.failure(Exception("Tidak ada koneksi internet"))
        } catch (e: Exception) {
            println("ERROR Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception(e.message ?: "Terjadi kesalahan"))
        }
    }

    // ==================== DELETE MENU ====================
    override suspend fun deleteMenu(id: Int): Result<Menu> = withContext(Dispatchers.IO) {
        try {
            val response = executeWithAuth {
                menuApiService.deleteMenu(id)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Gagal menghapus menu"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesi expired, silakan login kembali"
                    403 -> "Anda tidak memiliki akses"
                    404 -> "Menu tidak ditemukan"
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

    // ==================== HELPER: PREPARE IMAGE ====================
    private fun prepareImagePart(uri: Uri, context: Context): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        } catch (e: Exception) {
            null
        }
    }
}