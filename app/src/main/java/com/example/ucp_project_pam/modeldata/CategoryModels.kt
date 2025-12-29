package com.example.ucp_project_pam.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreateCategoryRequest(
    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String? = null
)

@Serializable
data class UpdateCategoryRequest(
    @SerialName("name")
    val name: String? = null,

    @SerialName("description")
    val description: String? = null
)


@Serializable
data class CategoryListResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("data")
    val data: List<Category>
)

@Serializable
data class CategoryResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String? = null,

    @SerialName("data")
    val data: Category? = null
)


@Serializable
data class Category(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String
)


sealed class CategoryUiState {
    object Idle : CategoryUiState()
    object Loading : CategoryUiState()
    data class Success(val categories: List<Category>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

sealed class CategoryDetailUiState {
    object Idle : CategoryDetailUiState()
    object Loading : CategoryDetailUiState()
    data class Success(val category: Category) : CategoryDetailUiState()
    data class Error(val message: String) : CategoryDetailUiState()
}

sealed class CategoryMutationUiState {
    object Idle : CategoryMutationUiState()
    object Loading : CategoryMutationUiState()
    data class Success(val message: String) : CategoryMutationUiState()
    data class Error(val message: String) : CategoryMutationUiState()
}