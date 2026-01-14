package com.example.ucp_project_pam.apiservice

import com.example.ucp_project_pam.modeldata.*
import retrofit2.Response
import retrofit2.http.*

interface CartApiService {

    // Get My Cart
    @GET("cart")
    suspend fun getMyCart(): Response<CartResponse>

    // Add Item to Cart
    @POST("items")
    suspend fun addItemToCart(
        @Body request: AddToCartRequest
    ): Response<CartResponse>

    // Update Cart Item
    @PUT("items")
    suspend fun updateCartItem(
        @Body request: UpdateCartItemRequest
    ): Response<CartResponse>

    // Remove Cart Item
    @DELETE("items/{menuId}")
    suspend fun removeCartItem(
        @Path("menuId") menuId: Int
    ): Response<CartResponse>

    // Clear Cart
    @DELETE("cart")
    suspend fun clearCart(): Response<CartResponse>
}