package com.example.ucp_project_pam.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== REQUEST MODELS ====================
@Serializable
data class CreateOrderRequest(
    @SerialName("address")
    val address: String
)

@Serializable
data class UpdateOrderStatusRequest(
    @SerialName("orderNumber")
    val orderNumber: String,

    @SerialName("status")
    val status: String
)

// ==================== RESPONSE MODELS ====================
@Serializable
data class OrderResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String? = null,

    @SerialName("data")
    val data: Order? = null
)

@Serializable
data class OrderListResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String? = null,

    @SerialName("data")
    val data: List<Order>
)

// ==================== ORDER MODELS ====================
@Serializable
data class Order(
    @SerialName("id")
    val id: Int,

    @SerialName("order_number")
    val orderNumber: String,

    @SerialName("status")
    val status: String, // PENDING, PROCESSING, COMPLETED, CANCELLED

    @SerialName("total_price")
    val totalPrice: Double,

    @SerialName("address")
    val address: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("items")
    val items: List<OrderItem>
)

@Serializable
data class OrderItem(
    @SerialName("id")
    val id: Int,

    @SerialName("menu_id")
    val menuId: Int,

    @SerialName("quantity")
    val quantity: Int,

    @SerialName("unit_price")
    val unitPrice: Double,

    @SerialName("subtotal")
    val subtotal: Double,

    @SerialName("menu")
    val menu: OrderItemMenu
)

@Serializable
data class OrderItemMenu(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("image_url")
    val imageUrl: String? = null
)

// ==================== ORDER STATUS ENUM ====================
enum class OrderStatus(val value: String, val displayName: String) {
    PENDING("PENDING", "Menunggu Konfirmasi"),
    PROCESSING("PROCESSING", "Diproses"),
    COMPLETED("COMPLETED", "Selesai"),
    CANCELLED("CANCELLED", "Dibatalkan");

    companion object {
        fun fromValue(value: String): OrderStatus {
            return values().find { it.value == value } ?: PENDING
        }
    }
}

// ==================== UI STATES ====================
sealed class OrderUiState {
    object Idle : OrderUiState()
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}

sealed class OrderDetailUiState {
    object Idle : OrderDetailUiState()
    object Loading : OrderDetailUiState()
    data class Success(val order: Order) : OrderDetailUiState()
    data class Error(val message: String) : OrderDetailUiState()
}

sealed class OrderMutationUiState {
    object Idle : OrderMutationUiState()
    object Loading : OrderMutationUiState()
    data class Success(val message: String, val order: Order? = null) : OrderMutationUiState()
    data class Error(val message: String) : OrderMutationUiState()
}
