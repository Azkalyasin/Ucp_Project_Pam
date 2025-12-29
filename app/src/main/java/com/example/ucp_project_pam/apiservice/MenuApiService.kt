package com.example.ucp_project_pam.apiservice

import com.example.ucp_project_pam.modeldata.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MenuApiService {
    @GET("menus")
    suspend fun getMenus(
        @Query("categoryId") categoryId: Int? = null,
        @Query("is_available") isAvailable: Boolean? = null,
        @Query("search") search: String? = null
    ): Response<MenuListResponse>

    @GET("menus/{id}")
    suspend fun getMenuById(
        @Path("id") id: Int
    ): Response<MenuResponse>

    @Multipart
    @POST("menus")
    suspend fun createMenu(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("price") price: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("is_available") isAvailable: RequestBody?,
        @Part("stock") stock: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<MenuResponse>

    @Multipart
    @PATCH("menus/{id}")
    suspend fun updateMenu(
        @Path("id") id: Int,
        @Part("name") name: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("price") price: RequestBody?,
        @Part("category_id") categoryId: RequestBody?,
        @Part("is_available") isAvailable: RequestBody?,
        @Part("stock") stock: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<MenuResponse>

    @DELETE("menus/{id}")
    suspend fun deleteMenu(
        @Path("id") id: Int
    ): Response<MenuResponse>
}