package com.example.ucp_project_pam.viewmodel.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.CartUiState
import com.example.ucp_project_pam.modeldata.CartMutationUiState
import com.example.ucp_project_pam.repositori.cart.RepositoryCart
import kotlinx.coroutines.launch

class CartViewModel(
    private val repositoryCart: RepositoryCart
) : ViewModel() {

    var cartUiState: CartUiState by mutableStateOf(CartUiState.Idle)
        private set

    var mutationState: CartMutationUiState by mutableStateOf(CartMutationUiState.Idle)
        private set

    // ==================== GET MY CART ====================
    fun getMyCart() {
        viewModelScope.launch {
            cartUiState = CartUiState.Loading

            val result = repositoryCart.getMyCart()

            cartUiState = if (result.isSuccess) {
                val cart = result.getOrNull()
                if (cart != null) {
                    CartUiState.Success(cart)
                } else {
                    CartUiState.Error("Keranjang kosong")
                }
            } else {
                CartUiState.Error(
                    result.exceptionOrNull()?.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

    // ==================== UPDATE QUANTITY (+ / -) ====================
    fun updateQuantity(menuId: Int, newQuantity: Int) {
        viewModelScope.launch {
            mutationState = CartMutationUiState.Loading

            val result = repositoryCart.updateCartItem(menuId, newQuantity)

            mutationState = if (result.isSuccess) {
                val cart = result.getOrNull()
                if (cart != null) {
                    // Update cart state langsung
                    cartUiState = CartUiState.Success(cart)
                    CartMutationUiState.Success(
                        message = "Keranjang diupdate",
                        cart = cart
                    )
                } else {
                    CartMutationUiState.Error("Gagal mengupdate")
                }
            } else {
                CartMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal mengupdate"
                )
            }
        }
    }

    // ==================== INCREASE QUANTITY ====================
    fun increaseQuantity(menuId: Int, currentQuantity: Int) {
        updateQuantity(menuId, currentQuantity + 1)
    }

    // ==================== DECREASE QUANTITY ====================
    fun decreaseQuantity(menuId: Int, currentQuantity: Int) {
        if (currentQuantity > 1) {
            updateQuantity(menuId, currentQuantity - 1)
        } else {
            // Jika quantity 1, hapus item
            removeItem(menuId)
        }
    }

    // ==================== REMOVE ITEM ====================
    fun removeItem(menuId: Int) {
        viewModelScope.launch {
            mutationState = CartMutationUiState.Loading

            val result = repositoryCart.removeCartItem(menuId)

            mutationState = if (result.isSuccess) {
                val cart = result.getOrNull()
                if (cart != null) {
                    // Update cart state langsung
                    cartUiState = CartUiState.Success(cart)
                    CartMutationUiState.Success(
                        message = "Item dihapus",
                        cart = cart
                    )
                } else {
                    CartMutationUiState.Error("Gagal menghapus")
                }
            } else {
                CartMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menghapus"
                )
            }
        }
    }

    // ==================== CLEAR CART ====================
    // ==================== CLEAR CART ====================
    fun clearCart() {
        viewModelScope.launch {
            mutationState = CartMutationUiState.Loading

            val result = repositoryCart.clearCart()

            if (result.isSuccess) {
                // Refresh cart setelah clear
                getMyCart()

                // Langsung set message sebagai String
                mutationState = CartMutationUiState.Success(message = "Keranjang dikosongkan")
            } else {
                mutationState = CartMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal mengosongkan keranjang"
                )
            }
        }
    }

    // ==================== REFRESH ====================
    fun refreshCart() {
        getMyCart()
    }

    // ==================== RESET ====================
    fun resetMutationState() {
        mutationState = CartMutationUiState.Idle
    }

    fun resetCartState() {
        cartUiState = CartUiState.Idle
    }
}