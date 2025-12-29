package com.example.ucp_project_pam.viewmodel.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.CategoryUiState
import com.example.ucp_project_pam.modeldata.CategoryMutationUiState
import com.example.ucp_project_pam.repositori.category.RepositoryCategory
import kotlinx.coroutines.launch

class CategoryListViewModel(
    private val repositoryCategory: RepositoryCategory
) : ViewModel() {

    var categoryUiState: CategoryUiState by mutableStateOf(CategoryUiState.Idle)
        private set

    var deleteMutationState: CategoryMutationUiState by mutableStateOf(CategoryMutationUiState.Idle)
        private set

    fun getAllCategories() {
        viewModelScope.launch {
            categoryUiState = CategoryUiState.Loading

            val result = repositoryCategory.getAllCategories()

            categoryUiState = if (result.isSuccess) {
                CategoryUiState.Success(result.getOrDefault(emptyList()))
            } else {
                CategoryUiState.Error(
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

    fun resetListState() {
        categoryUiState = CategoryUiState.Idle
    }
}