package com.example.ucp_project_pam.apiservice


import com.example.ucp_project_pam.modeldata.*
import retrofit2.Response
import retrofit2.http.*

interface OrderApiService {
    @POST("orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Response<OrderResponse>

    // Get Order by ID
    @GET("orders/{id}")
    suspend fun getOrderById(
        @Path("id") id: Int
    ): Response<OrderResponse>

    // Get My Orders (Customer)
    @GET("orders")
    suspend fun getMyOrders(): Response<OrderListResponse>

    @GET("orders/all")
    suspend fun getAllOrders(): Response<OrderListResponse>

    // Update Order Status (Admin)
    @PATCH("orders/status")
    suspend fun updateOrderStatus(
        @Body request: UpdateOrderStatusRequest
    ): Response<OrderResponse>
}