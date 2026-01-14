package com.example.ucp_project_pam.viewmodel.customer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.MenuDetailUiState
import com.example.ucp_project_pam.modeldata.CartMutationUiState
import com.example.ucp_project_pam.repositori.menu.RepositoryMenu
import com.example.ucp_project_pam.repositori.cart.RepositoryCart
import kotlinx.coroutines.launch

class CustomerMenuDetailViewModel(
    private val repositoryMenu: RepositoryMenu,
    private val repositoryCart: RepositoryCart
) : ViewModel() {

    var menuDetailUiState: MenuDetailUiState by mutableStateOf(MenuDetailUiState.Idle)
        private set

    var addToCartState: CartMutationUiState by mutableStateOf(CartMutationUiState.Idle)
        private set

    var selectedQuantity by mutableStateOf(1)
        private set

    var cartItemCount by mutableStateOf(0)
        private set

    // ==================== GET MENU DETAIL ====================
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

    // ==================== ADD TO CART WITH QUANTITY ====================
    fun addToCart(menuId: Int) {
        viewModelScope.launch {
            addToCartState = CartMutationUiState.Loading

            val result = repositoryCart.addItemToCart(menuId, selectedQuantity)

            addToCartState = if (result.isSuccess) {
                val cart = result.getOrNull()
                if (cart != null) {
                    cartItemCount = cart.totalQuantity
                    CartMutationUiState.Success(
                        message = "$selectedQuantity item ditambahkan ke keranjang",
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

    // ==================== QUANTITY SELECTOR ====================
    fun increaseQuantity(maxStock: Int?) {
        if (maxStock == null || selectedQuantity < maxStock) {
            selectedQuantity++
        }
    }

    fun decreaseQuantity() {
        if (selectedQuantity > 1) {
            selectedQuantity--
        }
    }

    fun setQuantity(quantity: Int) {
        if (quantity > 0) {
            selectedQuantity = quantity
        }
    }

    // ==================== RESET ====================
    fun resetQuantity() {
        selectedQuantity = 1
    }

    fun resetAddToCartState() {
        addToCartState = CartMutationUiState.Idle
    }

    fun resetDetailState() {
        menuDetailUiState = MenuDetailUiState.Idle
    }
}