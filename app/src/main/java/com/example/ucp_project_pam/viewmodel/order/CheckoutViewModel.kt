package com.example.ucp_project_pam.viewmodel.order

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.CartUiState
import com.example.ucp_project_pam.modeldata.OrderMutationUiState
import com.example.ucp_project_pam.repositori.cart.RepositoryCart
import com.example.ucp_project_pam.repositori.order.RepositoryOrder
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val repositoryCart: RepositoryCart,
    private val repositoryOrder: RepositoryOrder
) : ViewModel() {

    var cartUiState: CartUiState by mutableStateOf(CartUiState.Idle)
        private set

    var checkoutState: OrderMutationUiState by mutableStateOf(OrderMutationUiState.Idle)
        private set

    var addressFormState by mutableStateOf(AddressFormState())
        private set

    // ==================== GET CART (for checkout preview) ====================
    fun getCart() {
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

    // ==================== CREATE ORDER (CHECKOUT) ====================
    fun checkout() {
        viewModelScope.launch {
            val validatedForm = addressFormState.validate()
            addressFormState = validatedForm

            if (!validatedForm.isValid()) {
                checkoutState = OrderMutationUiState.Error(
                    "Mohon lengkapi alamat pengiriman"
                )
                return@launch
            }

            checkoutState = OrderMutationUiState.Loading

            val result = repositoryOrder.createOrder(addressFormState.address.trim())

            checkoutState = if (result.isSuccess) {
                val order = result.getOrNull()
                OrderMutationUiState.Success(
                    message = "Order berhasil dibuat!",
                    order = order
                )
            } else {
                OrderMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal membuat order"
                )
            }
        }
    }

    // ==================== FORM HANDLERS ====================
    fun updateAddress(address: String) {
        addressFormState = addressFormState.copy(
            address = address,
            addressError = null
        )
    }

    // ==================== RESET ====================
    fun resetCheckoutState() {
        checkoutState = OrderMutationUiState.Idle
    }

    fun resetForm() {
        addressFormState = AddressFormState()
    }

    fun resetCartState() {
        cartUiState = CartUiState.Idle
    }
}

// ==================== ADDRESS FORM STATE ====================
data class AddressFormState(
    val address: String = "",
    val addressError: String? = null
)

fun AddressFormState.isValid(): Boolean {
    return addressError == null && address.isNotBlank()
}

fun AddressFormState.validate(): AddressFormState {
    return copy(
        addressError = when {
            address.isBlank() -> "Alamat pengiriman tidak boleh kosong"
            address.length < 10 -> "Alamat minimal 10 karakter"
            address.length > 200 -> "Alamat maksimal 200 karakter"
            else -> null
        }
    )
}