package com.example.ucp_project_pam.viewmodel.provider


import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ucp_project_pam.repositori.AplikasiUmkm
import com.example.ucp_project_pam.viewmodel.AuthViewModel
import com.example.ucp_project_pam.viewmodel.ProfileViewModel
import com.example.ucp_project_pam.viewmodel.category.CategoryDetailViewModel
import com.example.ucp_project_pam.viewmodel.category.CategoryFormViewModel
import com.example.ucp_project_pam.viewmodel.category.CategoryListViewModel
import com.example.ucp_project_pam.viewmodel.menu.MenuDetailViewModel
import com.example.ucp_project_pam.viewmodel.menu.MenuFormViewModel
import com.example.ucp_project_pam.viewmodel.menu.MenuListViewModel


fun CreationExtras.aplikasiUmkm(): AplikasiUmkm =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
            as AplikasiUmkm

object PenyediaViewModel {

    val Factory = viewModelFactory {
        initializer {
            AuthViewModel(
                aplikasiUmkm().container.repositoryAuth
            )
        }

        // 👤 ProfileViewModel
        initializer {
            ProfileViewModel(
                aplikasiUmkm().container.repositoryAuth
            )
        }

        // ==================== CATEGORY ====================

        // 📂 CategoryListViewModel
        initializer {
            CategoryListViewModel(
                aplikasiUmkm().container.repositoryCategory
            )
        }

        // 👁️ CategoryDetailViewModel
        initializer {
            CategoryDetailViewModel(
                aplikasiUmkm().container.repositoryCategory
            )
        }

        // ✏️ CategoryFormViewModel
        initializer {
            CategoryFormViewModel(
                aplikasiUmkm().container.repositoryCategory
            )
        }

        // ==================== MENU ====================

        // 🍽️ MenuListViewModel
        initializer {
            MenuListViewModel(
                aplikasiUmkm().container.repositoryMenu
            )
        }

        // 👁️ MenuDetailViewModel
        initializer {
            MenuDetailViewModel(
                aplikasiUmkm().container.repositoryMenu
            )
        }

        // ✏️ MenuFormViewModel
        initializer {
            MenuFormViewModel(
                aplikasiUmkm().container.repositoryMenu
            )
        }
    }
}
