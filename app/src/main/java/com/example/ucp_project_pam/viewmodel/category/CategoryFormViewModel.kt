package com.example.ucp_project_pam.viewmodel.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.*
import com.example.ucp_project_pam.repositori.category.RepositoryCategory
import kotlinx.coroutines.launch

class CategoryFormViewModel(
    private val repositoryCategory: RepositoryCategory
) : ViewModel() {

    var categoryDetailUiState: CategoryDetailUiState by mutableStateOf(CategoryDetailUiState.Idle)
        private set

    var categoryMutationUiState: CategoryMutationUiState by mutableStateOf(CategoryMutationUiState.Idle)
        private set

    var categoryFormState by mutableStateOf(CategoryFormState())
        private set

    // Load category untuk edit mode
    fun getCategoryById(id: Int) {
        viewModelScope.launch {
            categoryDetailUiState = CategoryDetailUiState.Loading

            val result = repositoryCategory.getCategoryById(id)

            categoryDetailUiState = if (result.isSuccess) {
                val category = result.getOrNull()
                if (category != null) {
                    // Populate form
                    categoryFormState = CategoryFormState(
                        id = category.id,
                        name = category.name,
                        description = category.description ?: ""
                    )
                    CategoryDetailUiState.Success(category)
                } else {
                    CategoryDetailUiState.Error("Kategori tidak ditemukan")
                }
            } else {
                CategoryDetailUiState.Error(
                    result.exceptionOrNull()?.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

    fun createCategory() {
        viewModelScope.launch {
            val validatedForm = categoryFormState.validate()
            categoryFormState = validatedForm

            if (!validatedForm.isValid()) {
                categoryMutationUiState = CategoryMutationUiState.Error(
                    "Mohon lengkapi form dengan benar"
                )
                return@launch
            }

            categoryMutationUiState = CategoryMutationUiState.Loading

            val request = CreateCategoryRequest(
                name = categoryFormState.name.trim(),
                description = categoryFormState.description.trim()
                    .takeIf { it.isNotBlank() }
            )

            val result = repositoryCategory.createCategory(request)

            categoryMutationUiState = if (result.isSuccess) {
                CategoryMutationUiState.Success("Kategori berhasil ditambahkan")
            } else {
                CategoryMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menambahkan kategori"
                )
            }
        }
    }

    fun updateCategory() {
        if (categoryFormState.id <= 0) {
            categoryMutationUiState =
                CategoryMutationUiState.Error("ID kategori tidak valid")
            return
        }
        viewModelScope.launch {
            val validatedForm = categoryFormState.validate()
            categoryFormState = validatedForm

            if (!validatedForm.isValid()) {
                categoryMutationUiState = CategoryMutationUiState.Error(
                    "Mohon lengkapi form dengan benar"
                )
                return@launch
            }

            categoryMutationUiState = CategoryMutationUiState.Loading

            val request = UpdateCategoryRequest(
                name = categoryFormState.name.trim(),
                description = categoryFormState.description.trim()
                    .takeIf { it.isNotBlank() }
            )

            val result = repositoryCategory.updateCategory(
                categoryFormState.id,
                request
            )

            categoryMutationUiState = if (result.isSuccess) {
                CategoryMutationUiState.Success("Kategori berhasil diupdate")
            } else {
                CategoryMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal mengupdate kategori"
                )
            }
        }
    }

    // Form handlers
    fun updateName(name: String) {
        categoryFormState = categoryFormState.copy(
            name = name,
            nameError = null
        )
    }

    fun updateDescription(description: String) {
        categoryFormState = categoryFormState.copy(
            description = description
        )
    }

    fun resetForm() {
        categoryFormState = CategoryFormState()
    }

    fun resetMutationState() {
        categoryMutationUiState = CategoryMutationUiState.Idle
    }

    fun resetDetailState() {
        categoryDetailUiState = CategoryDetailUiState.Idle
    }
}

// ==================== FORM STATE ====================
data class CategoryFormState(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val nameError: String? = null
)

// ==================== VALIDATION ====================
fun CategoryFormState.isValid(): Boolean {
    return nameError == null && name.isNotBlank()
}

fun CategoryFormState.validate(): CategoryFormState {
    return copy(
        nameError = when {
            name.isBlank() -> "Nama kategori tidak boleh kosong"
            name.length < 3 -> "Nama kategori minimal 3 karakter"
            name.length > 100 -> "Nama kategori maksimal 100 karakter"
            else -> null
        }
    )
}