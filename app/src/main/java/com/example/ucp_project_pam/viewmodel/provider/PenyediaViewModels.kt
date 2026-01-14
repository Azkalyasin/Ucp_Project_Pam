package com.example.ucp_project_pam.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ucp_project_pam.repositori.AplikasiUmkm
import com.example.ucp_project_pam.viewmodel.AuthViewModel
import com.example.ucp_project_pam.viewmodel.ProfileViewModel
import com.example.ucp_project_pam.viewmodel.category.CategoryListViewModel
import com.example.ucp_project_pam.viewmodel.category.CategoryDetailViewModel
import com.example.ucp_project_pam.viewmodel.category.CategoryFormViewModel
import com.example.ucp_project_pam.viewmodel.menu.MenuListViewModel
import com.example.ucp_project_pam.viewmodel.menu.MenuDetailViewModel
import com.example.ucp_project_pam.viewmodel.menu.MenuFormViewModel
import com.example.ucp_project_pam.viewmodel.customer.CustomerMenuViewModel
import com.example.ucp_project_pam.viewmodel.customer.CustomerMenuDetailViewModel
import com.example.ucp_project_pam.viewmodel.cart.CartViewModel
import com.example.ucp_project_pam.viewmodel.order.AdminOrderViewModel
import com.example.ucp_project_pam.viewmodel.order.CheckoutViewModel
import com.example.ucp_project_pam.viewmodel.order.CustomerOrderViewModel

fun CreationExtras.aplikasiUmkm(): AplikasiUmkm =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
            as AplikasiUmkm

object PenyediaViewModel {

    val Factory = viewModelFactory {

        // ==================== AUTH ====================

        initializer {
            AuthViewModel(aplikasiUmkm().container.repositoryAuth)
        }

        initializer {
            ProfileViewModel(aplikasiUmkm().container.repositoryAuth)
        }

        // ==================== CATEGORY (ADMIN) ====================

        initializer {
            CategoryListViewModel(aplikasiUmkm().container.repositoryCategory)
        }

        initializer {
            CategoryDetailViewModel(aplikasiUmkm().container.repositoryCategory)
        }

        initializer {
            CategoryFormViewModel(aplikasiUmkm().container.repositoryCategory)
        }

        // ==================== MENU (ADMIN) ====================

        initializer {
            MenuListViewModel(aplikasiUmkm().container.repositoryMenu)
        }

        initializer {
            MenuDetailViewModel(aplikasiUmkm().container.repositoryMenu)
        }

        initializer {
            MenuFormViewModel(aplikasiUmkm().container.repositoryMenu)
        }

        // ==================== CUSTOMER MENU ✅ BARU ====================

        initializer {
            CustomerMenuViewModel(
                aplikasiUmkm().container.repositoryMenu,
                aplikasiUmkm().container.repositoryCart
            )
        }

        initializer {
            CustomerMenuDetailViewModel(
                aplikasiUmkm().container.repositoryMenu,
                aplikasiUmkm().container.repositoryCart
            )
        }

        // ==================== CART ✅ BARU ====================

        initializer {
            CartViewModel(aplikasiUmkm().container.repositoryCart)
        }

        initializer {
            CustomerOrderViewModel(aplikasiUmkm().container.repositoryOrder)
        }

        initializer {
            CheckoutViewModel(
                aplikasiUmkm().container.repositoryCart,
                aplikasiUmkm().container.repositoryOrder
            )
        }

        // ==================== ORDER (ADMIN) ✅ BARU ====================

        initializer {
            AdminOrderViewModel(aplikasiUmkm().container.repositoryOrder)
        }
    }
}