package com.example.ucp_project_pam.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== REQUEST MODELS ====================
@Serializable
data class CreateMenuRequest(
    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("price")
    val price: Double,

    @SerialName("category_id")
    val categoryId: Int,

    @SerialName("is_available")
    val isAvailable: Boolean = true,

    @SerialName("stock")
    val stock: Int? = null
)

@Serializable
data class UpdateMenuRequest(
    @SerialName("name")
    val name: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("price")
    val price: Double? = null,

    @SerialName("category_id")
    val categoryId: Int? = null,

    @SerialName("is_available")
    val isAvailable: Boolean? = null,

    @SerialName("stock")
    val stock: Int? = null
)

// ==================== RESPONSE MODELS ====================
@Serializable
data class MenuListResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("data")
    val data: List<Menu>
)

@Serializable
data class MenuResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String? = null,

    @SerialName("data")
    val data: Menu? = null
)

// ==================== MENU MODEL ====================
@Serializable
data class Menu(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("price")
    val price: Double,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("is_available")
    val isAvailable: Boolean,

    @SerialName("stock")
    val stock: Int? = null,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("category")
    val category: MenuCategory
)

@Serializable
data class MenuCategory(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String
)

// ==================== UI STATES ====================
sealed class MenuUiState {
    object Idle : MenuUiState()
    object Loading : MenuUiState()
    data class Success(val menus: List<Menu>) : MenuUiState()
    data class Error(val message: String) : MenuUiState()
}

sealed class MenuDetailUiState {
    object Idle : MenuDetailUiState()
    object Loading : MenuDetailUiState()
    data class Success(val menu: Menu) : MenuDetailUiState()
    data class Error(val message: String) : MenuDetailUiState()
}

sealed class MenuMutationUiState {
    object Idle : MenuMutationUiState()
    object Loading : MenuMutationUiState()
    data class Success(val message: String) : MenuMutationUiState()
    data class Error(val message: String) : MenuMutationUiState()
}