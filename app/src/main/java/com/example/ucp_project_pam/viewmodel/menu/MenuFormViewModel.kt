package com.example.ucp_project_pam.viewmodel.menu

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.MenuDetailUiState
import com.example.ucp_project_pam.modeldata.MenuMutationUiState
import com.example.ucp_project_pam.repositori.menu.RepositoryMenu
import kotlinx.coroutines.launch

class MenuFormViewModel(
    private val repositoryMenu: RepositoryMenu
) : ViewModel() {

    var menuDetailUiState: MenuDetailUiState by mutableStateOf(MenuDetailUiState.Idle)
        private set

    var menuMutationUiState: MenuMutationUiState by mutableStateOf(MenuMutationUiState.Idle)
        private set

    var menuFormState by mutableStateOf(MenuFormState())
        private set

    fun getMenuById(id: Int) {
        viewModelScope.launch {
            menuDetailUiState = MenuDetailUiState.Loading

            val result = repositoryMenu.getMenuById(id)

            menuDetailUiState = if (result.isSuccess) {
                val menu = result.getOrNull()
                if (menu != null) {
                    // Populate form
                    menuFormState = MenuFormState(
                        id = menu.id,
                        name = menu.name,
                        description = menu.description ?: "",
                        price = menu.price.toString(),
                        categoryId = menu.category.id,
                        categoryName = menu.category.name,
                        isAvailable = menu.isAvailable,
                        stock = menu.stock?.toString() ?: "",
                        imageUri = menu.imageUrl
                    )
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

    fun createMenu(context: Context) {
        viewModelScope.launch {
            val validatedForm = menuFormState.validate()
            menuFormState = validatedForm

            if (!validatedForm.isValid()) {
                menuMutationUiState = MenuMutationUiState.Error(
                    "Mohon lengkapi form dengan benar"
                )
                return@launch
            }

            menuMutationUiState = MenuMutationUiState.Loading

            val result = repositoryMenu.createMenu(menuFormState, context)

            menuMutationUiState = if (result.isSuccess) {
                MenuMutationUiState.Success("Menu berhasil ditambahkan")
            } else {
                MenuMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal menambahkan menu"
                )
            }
        }
    }

    fun updateMenu(context: Context) {
        viewModelScope.launch {
            val validatedForm = menuFormState.validate()
            menuFormState = validatedForm

            if (!validatedForm.isValid()) {
                menuMutationUiState = MenuMutationUiState.Error(
                    "Mohon lengkapi form dengan benar"
                )
                return@launch
            }

            menuMutationUiState = MenuMutationUiState.Loading

            val result = repositoryMenu.updateMenu(menuFormState, context)

            menuMutationUiState = if (result.isSuccess) {
                MenuMutationUiState.Success("Menu berhasil diupdate")
            } else {
                MenuMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal mengupdate menu"
                )
            }
        }
    }

    // Form handlers
    fun updateName(name: String) {
        menuFormState = menuFormState.copy(name = name, nameError = null)
    }

    fun updateDescription(description: String) {
        menuFormState = menuFormState.copy(description = description)
    }

    fun updatePrice(price: String) {
        menuFormState = menuFormState.copy(price = price, priceError = null)
    }

    fun updateCategory(categoryId: Int, categoryName: String) {
        menuFormState = menuFormState.copy(
            categoryId = categoryId,
            categoryName = categoryName,
            categoryError = null
        )
    }

    fun updateStock(stock: String) {
        menuFormState = menuFormState.copy(stock = stock, stockError = null)
    }

    fun updateIsAvailable(isAvailable: Boolean) {
        menuFormState = menuFormState.copy(isAvailable = isAvailable)
    }

    fun updateImageUri(uri: Uri?) {
        menuFormState = menuFormState.copy(imageUri = uri?.toString())
    }

    fun resetForm() {
        menuFormState = MenuFormState()
    }

    fun resetMutationState() {
        menuMutationUiState = MenuMutationUiState.Idle
    }

    fun resetDetailState() {
        menuDetailUiState = MenuDetailUiState.Idle
    }
}

// ==================== FORM STATE ====================
data class MenuFormState(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val categoryId: Int = 0,
    val categoryName: String = "",
    val isAvailable: Boolean = true,
    val stock: String = "",
    val imageUri: String? = null,

    val nameError: String? = null,
    val priceError: String? = null,
    val categoryError: String? = null,
    val stockError: String? = null
)

fun MenuFormState.isValid(): Boolean {
    return nameError == null &&
            priceError == null &&
            categoryError == null &&
            stockError == null &&
            name.isNotBlank() &&
            price.isNotBlank() &&
            categoryId > 0
}

fun MenuFormState.validate(): MenuFormState {
    return copy(
        nameError = when {
            name.isBlank() -> "Nama menu tidak boleh kosong"
            name.length < 3 -> "Nama menu minimal 3 karakter"
            name.length > 100 -> "Nama menu maksimal 100 karakter"
            else -> null
        },
        priceError = when {
            price.isBlank() -> "Harga tidak boleh kosong"
            price.toDoubleOrNull() == null -> "Harga harus berupa angka"
            price.toDoubleOrNull()!! <= 0 -> "Harga harus lebih dari 0"
            else -> null
        },
        categoryError = when {
            categoryId <= 0 -> "Pilih kategori"
            else -> null
        },
        stockError = when {
            stock.isNotBlank() && stock.toIntOrNull() == null -> "Stock harus berupa angka"
            stock.isNotBlank() && stock.toIntOrNull()!! < 0 -> "Stock tidak boleh negatif"
            else -> null
        }
    )
}

// ==================== VALIDATION ====================
