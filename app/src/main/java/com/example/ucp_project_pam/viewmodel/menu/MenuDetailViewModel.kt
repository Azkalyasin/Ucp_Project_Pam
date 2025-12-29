package com.example.ucp_project_pam.viewmodel.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.MenuDetailUiState
import com.example.ucp_project_pam.modeldata.MenuMutationUiState
import com.example.ucp_project_pam.repositori.menu.RepositoryMenu
import kotlinx.coroutines.launch

class MenuDetailViewModel(
    private val repositoryMenu: RepositoryMenu
) : ViewModel() {

    var menuDetailUiState: MenuDetailUiState by mutableStateOf(MenuDetailUiState.Idle)
        private set

    var deleteMutationState: MenuMutationUiState by mutableStateOf(MenuMutationUiState.Idle)
        private set

    fun getMenuById(id: Int) {
        viewModelScope.launch {
            menuDetailUiState = MenuDetailUiState.Loading

            val result = repositoryMenu.getMenuById(id)

            menuDetailUiState = if (result.isSuccess) {
                val menu = result.getOrNull()
                if (menu != null) {
                    MenuDetailUiState.Success(menu)
                } else {
                    MenuDetailUiState.Error("Menu tidak ditemukan")
                }
            } else {
                MenuDetailUiState.Error(
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

    fun resetDeleteState() {
        deleteMutationState = MenuMutationUiState.Idle
    }

    fun resetDetailState() {
        menuDetailUiState = MenuDetailUiState.Idle
    }
}