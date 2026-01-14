package com.example.ucp_project_pam.viewmodel.customer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.MenuUiState
import com.example.ucp_project_pam.modeldata.CartMutationUiState
import com.example.ucp_project_pam.repositori.menu.RepositoryMenu
import com.example.ucp_project_pam.repositori.cart.RepositoryCart
import com.example.ucp_project_pam.modeldata.MenuFilterState
import kotlinx.coroutines.launch

class CustomerMenuViewModel(
    private val repositoryMenu: RepositoryMenu,
    private val repositoryCart: RepositoryCart
) : ViewModel() {

    var menuUiState: MenuUiState by mutableStateOf(MenuUiState.Idle)
        private set

    var addToCartState: CartMutationUiState by mutableStateOf(CartMutationUiState.Idle)
        private set

    var filterState by mutableStateOf(MenuFilterState())
        private set

    var cartItemCount by mutableStateOf(0)
        private set

    // ==================== GET ALL MENUS (CUSTOMER) ====================
    fun getAllMenus() {
        viewModelScope.launch {
            menuUiState = MenuUiState.Loading

            val result = repositoryMenu.getAllMenus(
                categoryId = filterState.categoryId,
                isAvailable = true, // Hanya tampilkan menu yang available
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

    // ==================== ADD TO CART (Quick Add) ====================
    fun addToCart(menuId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            addToCartState = CartMutationUiState.Loading

            val result = repositoryCart.addItemToCart(menuId, quantity)

            addToCartState = if (result.isSuccess) {
                val cart = result.getOrNull()
                if (cart != null) {
                    cartItemCount = cart.totalQuantity
                    CartMutationUiState.Success(
                        message = "Ditambahkan ke keranjang",
                        cart = cart
                    )
                } else {
                    CartMutationUiState.Error("Gagal menambahkan")
                }
            } else {
                CartMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menambahkan"
                )
            }
        }
    }

    // ==================== GET CART COUNT ====================
    fun getCartCount() {
        viewModelScope.launch {
            val result = repositoryCart.getMyCart()
            if (result.isSuccess) {
                val cart = result.getOrNull()
                cartItemCount = cart?.totalQuantity ?: 0
            }
        }
    }

    // ==================== FILTER ====================
    fun updateSearchQuery(query: String) {
        filterState = filterState.copy(searchQuery = query)
    }

    fun updateCategoryFilter(categoryId: Int?) {
        filterState = filterState.copy(categoryId = categoryId)
        getAllMenus()
    }

    fun clearFilter() {
        filterState = MenuFilterState()
        getAllMenus()
    }

    // ==================== RESET ====================
    fun resetAddToCartState() {
        addToCartState = CartMutationUiState.Idle
    }

    fun resetMenuState() {
        menuUiState = MenuUiState.Idle
    }
}