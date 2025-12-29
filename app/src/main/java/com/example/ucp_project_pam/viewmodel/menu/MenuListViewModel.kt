package com.example.ucp_project_pam.viewmodel.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.MenuUiState
import com.example.ucp_project_pam.modeldata.MenuMutationUiState
import com.example.ucp_project_pam.repositori.menu.RepositoryMenu
import kotlinx.coroutines.launch

class MenuListViewModel(
    private val repositoryMenu: RepositoryMenu
) : ViewModel() {

    var menuUiState: MenuUiState by mutableStateOf(MenuUiState.Idle)
        private set

    var deleteMutationState: MenuMutationUiState by mutableStateOf(MenuMutationUiState.Idle)
        private set

    var filterState by mutableStateOf(MenuFilterState())
        private set

    fun getAllMenus() {
        viewModelScope.launch {
            menuUiState = MenuUiState.Loading

            val result = repositoryMenu.getAllMenus(
                categoryId = filterState.categoryId,
                isAvailable = filterState.isAvailable,
                search = filterState.searchQuery.takeIf { it.isNotBlank() }
            )

            menuUiState = if (result.isSuccess) {
                MenuUiState.Success(result.getOrDefault(emptyList()))
            } else {
                MenuUiState.Error(
                    result.exceptionOrNull()?.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

    fun deleteMenu(id: Int) {
        viewModelScope.launch {
            deleteMutationState = MenuMutationUiState.Loading

            val result = repositoryMenu.deleteMenu(id)

            deleteMutationState = if (result.isSuccess) {
                MenuMutationUiState.Success("Menu berhasil dihapus")
            } else {
                MenuMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menghapus menu"
                )
            }
        }
    }

    fun updateFilter(newFilter: MenuFilterState) {
        filterState = newFilter
        getAllMenus()
    }

    fun updateSearchQuery(query: String) {
        filterState = filterState.copy(searchQuery = query)
    }

    fun clearFilter() {
        filterState = MenuFilterState()
        getAllMenus()
    }

    fun resetDeleteState() {
        deleteMutationState = MenuMutationUiState.Idle
    }

    fun resetListState() {
        menuUiState = MenuUiState.Idle
    }
}

data class MenuFilterState(
    val categoryId: Int? = null,
    val isAvailable: Boolean? = null,
    val searchQuery: String = ""
)