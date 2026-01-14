package com.example.ucp_project_pam.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== REQUEST MODELS ====================
@Serializable
data class AddToCartRequest(
    @SerialName("menuId")
    val menuId: Int,

    @SerialName("quantity")
    val quantity: Int
)

@Serializable
data class UpdateCartItemRequest(
    @SerialName("menuId")
    val menuId: Int,

    @SerialName("quantity")
    val quantity: Int
)

// ==================== RESPONSE MODELS ====================
@Serializable
data class CartResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String? = null,

    @SerialName("data")
    val data: Cart? = null
)

@Serializable
data class CartItemResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String? = null,

    @SerialName("data")
    val data: CartItem? = null
)

// ==================== CART MODELS ====================
@Serializable
data class Cart(
    @SerialName("id")
    val id: Int,

    @SerialName("user_id")
    val userId: Int,

    @SerialName("items")
    val items: List<CartItem>,

    @SerialName("total_quantity")
    val totalQuantity: Int,

    @SerialName("total_price")
    val totalPrice: Double
)

@Serializable
data class CartItem(
    @SerialName("id")
    val id: Int,

    @SerialName("quantity")
    val quantity: Int,

    @SerialName("menu")
    val menu: CartItemMenu,

    @SerialName("subtotal")
    val subtotal: Double
)

@Serializable
data class CartItemMenu(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("price")
    val price: Double,

    @SerialName("image_url")
    val imageUrl: String? = null
)

// ==================== UI STATES ====================
sealed class CartUiState {
    object Idle : CartUiState()
    object Loading : CartUiState()
    data class Success(val cart: Cart) : CartUiState()
    data class Error(val message: String) : CartUiState()
}

sealed class CartMutationUiState {
    object Idle : CartMutationUiState()
    object Loading : CartMutationUiState()
    data class Success(val message: String, val cart: Cart? = null) : CartMutationUiState()
    data class Error(val message: String) : CartMutationUiState()
}