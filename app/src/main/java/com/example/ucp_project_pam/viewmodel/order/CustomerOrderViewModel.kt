package com.example.ucp_project_pam.viewmodel.order

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.OrderUiState
import com.example.ucp_project_pam.modeldata.OrderDetailUiState
import com.example.ucp_project_pam.repositori.order.RepositoryOrder
import kotlinx.coroutines.launch

class CustomerOrderViewModel(
    private val repositoryOrder: RepositoryOrder
) : ViewModel() {

    var orderUiState: OrderUiState by mutableStateOf(OrderUiState.Idle)
        private set

    var orderDetailUiState: OrderDetailUiState by mutableStateOf(OrderDetailUiState.Idle)
        private set

    // ==================== GET MY ORDERS ====================
    fun getMyOrders() {
        viewModelScope.launch {
            orderUiState = OrderUiState.Loading

            val result = repositoryOrder.getMyOrders()

            orderUiState = if (result.isSuccess) {
                OrderUiState.Success(result.getOrDefault(emptyList()))
            } else {
                OrderUiState.Error(
                    result.exceptionOrNull()?.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

    // ==================== GET ORDER BY ID ====================
    fun getOrderById(id: Int) {
        viewModelScope.launch {
            orderDetailUiState = OrderDetailUiState.Loading

            val result = repositoryOrder.getOrderById(id)

            orderDetailUiState = if (result.isSuccess) {
                val order = result.getOrNull()
                if (order != null) {
                    OrderDetailUiState.Success(order)
                } else {
                    OrderDetailUiState.Error("Order tidak ditemukan")
                }
            } else {
                OrderDetailUiState.Error(
                    result.exceptionOrNull()?.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

    // ==================== REFRESH ====================
    fun refreshOrders() {
        getMyOrders()
    }

    // ==================== RESET ====================
    fun resetOrderState() {
        orderUiState = OrderUiState.Idle
    }

    fun resetDetailState() {
        orderDetailUiState = OrderDetailUiState.Idle
    }
}