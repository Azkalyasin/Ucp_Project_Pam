package com.example.ucp_project_pam.apiservice


import com.example.ucp_project_pam.modeldata.CategoryListResponse
import com.example.ucp_project_pam.modeldata.CategoryResponse
import com.example.ucp_project_pam.modeldata.CreateCategoryRequest
import com.example.ucp_project_pam.modeldata.UpdateCategoryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryApiService {
    @GET("categories")
    suspend fun getCategories(): Response<CategoryListResponse>

    @GET("categories/{id}")
    suspend fun getCategoryById(
        @Path("id") id: Int
    ): Response<CategoryResponse>

    @POST("categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): Response<CategoryResponse>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body request: UpdateCategoryRequest
    ): Response<CategoryResponse>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: Int
    ): Response<CategoryResponse>
}