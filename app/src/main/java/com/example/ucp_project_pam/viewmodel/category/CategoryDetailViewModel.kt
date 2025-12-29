package com.example.ucp_project_pam.viewmodel.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.CategoryDetailUiState
import com.example.ucp_project_pam.modeldata.CategoryMutationUiState
import com.example.ucp_project_pam.repositori.category.RepositoryCategory
import kotlinx.coroutines.launch

class CategoryDetailViewModel(
    private val repositoryCategory: RepositoryCategory
) : ViewModel() {

    var categoryDetailUiState: CategoryDetailUiState by mutableStateOf(CategoryDetailUiState.Idle)
        private set

    var deleteMutationState: CategoryMutationUiState by mutableStateOf(CategoryMutationUiState.Idle)
        private set

    fun getCategoryById(id: Int) {
        viewModelScope.launch {
            categoryDetailUiState = CategoryDetailUiState.Loading

            val result = repositoryCategory.getCategoryById(id)

            categoryDetailUiState = if (result.isSuccess) {
                val category = result.getOrNull()
                if (category != null) {
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

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            deleteMutationState = CategoryMutationUiState.Loading

            val result = repositoryCategory.deleteCategory(id)

            deleteMutationState = if (result.isSuccess) {
                CategoryMutationUiState.Success("Kategori berhasil dihapus")
            } else {
                CategoryMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menghapus kategori"
                )
            }
        }
    }

    fun resetDeleteState() {
        deleteMutationState = CategoryMutationUiState.Idle
    }

    fun resetDetailState() {
        categoryDetailUiState = CategoryDetailUiState.Idle
    }
}