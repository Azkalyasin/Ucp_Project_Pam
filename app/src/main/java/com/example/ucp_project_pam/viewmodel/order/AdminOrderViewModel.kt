package com.example.ucp_project_pam.viewmodel.order

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp_project_pam.modeldata.OrderUiState
import com.example.ucp_project_pam.modeldata.OrderDetailUiState
import com.example.ucp_project_pam.modeldata.OrderMutationUiState
import com.example.ucp_project_pam.modeldata.OrderStatus
import com.example.ucp_project_pam.repositori.order.RepositoryOrder
import kotlinx.coroutines.launch

class AdminOrderViewModel(
    private val repositoryOrder: RepositoryOrder
) : ViewModel() {

    var orderUiState: OrderUiState by mutableStateOf(OrderUiState.Idle)
        private set

    var orderDetailUiState: OrderDetailUiState by mutableStateOf(OrderDetailUiState.Idle)
        private set

    var updateStatusState: OrderMutationUiState by mutableStateOf(OrderMutationUiState.Idle)
        private set

    var filterStatus by mutableStateOf<OrderStatus?>(null)
        private set

    // ==================== GET ALL ORDERS (ADMIN) ====================
    fun getAllOrders() {
        viewModelScope.launch {
            orderUiState = OrderUiState.Loading

            val result = repositoryOrder.getAllOrders() // Admin dapat semua order

            orderUiState = if (result.isSuccess) {
                val orders = result.getOrDefault(emptyList())

                // Filter by status if selected
                val filteredOrders = if (filterStatus != null) {
                    orders.filter { it.status == filterStatus!!.value }
                } else {
                    orders
                }

                OrderUiState.Success(filteredOrders)
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

    // ==================== UPDATE ORDER STATUS ====================
    fun updateOrderStatus(orderNumber: String, status: OrderStatus) {
        viewModelScope.launch {
            updateStatusState = OrderMutationUiState.Loading

            val result = repositoryOrder.updateOrderStatus(orderNumber, status.value)

            updateStatusState = if (result.isSuccess) {
                val order = result.getOrNull()
                OrderMutationUiState.Success(
                    message = "Status order berhasil diupdate",
                    order = order
                )
            } else {
                OrderMutationUiState.Error(
                    result.exceptionOrNull()?.message ?: "Gagal mengupdate status"
                )
            }
        }
    }

    // ==================== FILTER ====================
    fun updateFilterStatus(status: OrderStatus?) {
        filterStatus = status
        getAllOrders()
    }

    fun clearFilter() {
        filterStatus = null
        getAllOrders()
    }

    // ==================== REFRESH ====================
    fun refreshOrders() {
        getAllOrders()
    }

    // ==================== RESET ====================
    fun resetUpdateStatusState() {
        updateStatusState = OrderMutationUiState.Idle
    }

    fun resetOrderState() {
        orderUiState = OrderUiState.Idle
    }

    fun resetDetailState() {
        orderDetailUiState = OrderDetailUiState.Idle
    }
}