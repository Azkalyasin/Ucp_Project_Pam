
package com.example.ucp_project_pam.repositori

import android.app.Application
import android.content.Context
import com.example.ucp_project_pam.apiservice.AuthApiService
import com.example.ucp_project_pam.apiservice.CategoryApiService
import com.example.ucp_project_pam.apiservice.MenuApiService
import com.example.ucp_project_pam.apiservice.CartApiService
import com.example.ucp_project_pam.apiservice.OrderApiService
import com.example.ucp_project_pam.data.TokenManager
import com.example.ucp_project_pam.repositori.auth.JaringanRepositoryAuth
import com.example.ucp_project_pam.repositori.auth.RepositoryAuth
import com.example.ucp_project_pam.repositori.cart.JaringanRepositoryCart
import com.example.ucp_project_pam.repositori.cart.RepositoryCart
import com.example.ucp_project_pam.repositori.category.JaringanRepositoryCategory
import com.example.ucp_project_pam.repositori.category.RepositoryCategory
import com.example.ucp_project_pam.repositori.menu.JaringanRepositoryMenu
import com.example.ucp_project_pam.repositori.menu.RepositoryMenu
import com.example.ucp_project_pam.repositori.order.JaringanRepositoryOrder
import com.example.ucp_project_pam.repositori.order.RepositoryOrder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

interface ContainerApp {
    val repositoryAuth: RepositoryAuth
    val tokenManager: TokenManager
    val repositoryCategory: RepositoryCategory
    val repositoryMenu: RepositoryMenu
    val repositoryCart: RepositoryCart

    val repositoryOrder: RepositoryOrder

}

class DefaultContainerApp(private val context: Context) : ContainerApp {

    private val baseUrl = "http://10.0.2.2:5000/api/v1/"

    override val tokenManager: TokenManager by lazy {
        TokenManager(context)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = tokenManager.getAccessToken()

        val newRequest = if (token != null &&
            !originalRequest.url.encodedPath.contains("login") &&
            !originalRequest.url.encodedPath.contains("register")) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        chain.proceed(newRequest)
    }


    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true // Handle null values
                isLenient = true
            }.asConverterFactory("application/json".toMediaType())
        )
        .client(client)
        .build()

    // ==================== API SERVICES ====================
    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val categoryApiService: CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }

    private val menuApiService: MenuApiService by lazy {
        retrofit.create(MenuApiService::class.java)
    }

    private val cartApiService: CartApiService by lazy { // ✅ Tambah ini
        retrofit.create(CartApiService::class.java)
    }

    private val orderApiService: OrderApiService by lazy {
        retrofit.create(OrderApiService::class.java)
    }


    // ==================== REPOSITORIES ====================
    override val repositoryAuth: RepositoryAuth by lazy {
        JaringanRepositoryAuth(authApiService, tokenManager)
    }

    override val repositoryCategory: RepositoryCategory by lazy {
        JaringanRepositoryCategory(categoryApiService, tokenManager, repositoryAuth)
    }

    override val repositoryMenu: RepositoryMenu by lazy {
        JaringanRepositoryMenu(menuApiService, tokenManager, repositoryAuth)
    }

    override val repositoryCart: RepositoryCart by lazy {
        JaringanRepositoryCart(cartApiService, tokenManager, repositoryAuth)
    }

    override val repositoryOrder: RepositoryOrder by lazy {
        JaringanRepositoryOrder(orderApiService, tokenManager, repositoryAuth)
    }
}

class AplikasiUmkm : Application() {
    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        container = DefaultContainerApp(this)
    }
}