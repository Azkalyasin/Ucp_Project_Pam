package com.example.ucp_project_pam.view.admin.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ucp_project_pam.modeldata.Order
import com.example.ucp_project_pam.modeldata.OrderDetailUiState
import com.example.ucp_project_pam.modeldata.OrderMutationUiState
import com.example.ucp_project_pam.modeldata.OrderStatus
import com.example.ucp_project_pam.viewmodel.order.AdminOrderViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    orderId: Int,
    onNavigateBack: () -> Unit,
    viewModel: AdminOrderViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val detailState by remember { derivedStateOf { viewModel.orderDetailUiState } }
    val updateStatusState by remember { derivedStateOf { viewModel.updateStatusState } }

    var showStatusDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load order detail
    LaunchedEffect(orderId) {
        viewModel.getOrderById(orderId)
    }

    // Handle update status result
    LaunchedEffect(updateStatusState) {
        when (updateStatusState) {
            is OrderMutationUiState.Success -> {
                val message = (updateStatusState as OrderMutationUiState.Success).message
                snackbarHostState.showSnackbar(message)
                viewModel.resetUpdateStatusState()
                viewModel.getOrderById(orderId)
            }
            is OrderMutationUiState.Error -> {
                val message = (updateStatusState as OrderMutationUiState.Error).message
                snackbarHostState.showSnackbar(message)
                viewModel.resetUpdateStatusState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    if (detailState is OrderDetailUiState.Success) {
                        val order = (detailState as OrderDetailUiState.Success).order
                        val status = OrderStatus.fromValue(order.status)

                        if (status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED) {
                            IconButton(onClick = { showStatusDialog = true }) {
                                Icon(Icons.Default.Edit, "Update Status")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (detailState) {
                is OrderDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is OrderDetailUiState.Success -> {
                    val order = (detailState as OrderDetailUiState.Success).order
                    AdminOrderDetailContent(order = order)
                }

                is OrderDetailUiState.Error -> {
                    ErrorAdminOrderDetailState(
                        message = (detailState as OrderDetailUiState.Error).message,
                        onRetry = { viewModel.getOrderById(orderId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }
        }

        // Update Status Dialog
        if (showStatusDialog && detailState is OrderDetailUiState.Success) {
            val order = (detailState as OrderDetailUiState.Success).order
            UpdateStatusDialog(
                currentOrder = order,
                onDismiss = { showStatusDialog = false },
                onUpdateStatus = { newStatus ->
                    viewModel.updateOrderStatus(order.orderNumber, newStatus)
                    showStatusDialog = false
                },
                isLoading = updateStatusState is OrderMutationUiState.Loading
            )
        }
    }
}

@Composable
fun AdminOrderDetailContent(order: Order) {
    val status = OrderStatus.fromValue(order.status)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Order Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Order #${order.orderNumber}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatDate(order.createdAt),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }

                        OrderStatusBadge(status = status)
                    }

                    Divider()

                    Text(
                        text = "Order ID: ${order.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Status Info Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Status Saat Ini",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = status.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        // Address Card
        item {
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Alamat Pengiriman",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = order.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Items Section Header
        item {
            Text(
                text = "Item Pesanan (${order.items.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Order Items
        items(order.items, key = { it.id }) { item ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Image
                    if (item.menu.imageUrl != null) {
                        AsyncImage(
                            model = item.menu.imageUrl,
                            contentDescription = item.menu.name,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Surface(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Item Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.menu.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Menu ID: ${item.menuId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatRupiah(item.unitPrice),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "×",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${item.quantity}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Subtotal
                    Text(
                        text = formatRupiah(item.subtotal),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Total Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ringkasan Pembayaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Pembayaran",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatRupiah(order.totalPrice),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Timestamps
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Informasi Waktu",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Dibuat",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatDate(order.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Terakhir Update",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatDate(order.updatedAt),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    } // ✅ CLOSING LazyColumn
}

// ✅ MOVE TO TOP-LEVEL (outside AdminOrderDetailContent)
@Composable
fun UpdateStatusDialog(
    currentOrder: Order,
    onDismiss: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit,
    isLoading: Boolean
) {
    val currentStatus = OrderStatus.fromValue(currentOrder.status)

    val availableStatuses = when (currentStatus) {
        OrderStatus.PENDING -> listOf(OrderStatus.PROCESSING, OrderStatus.CANCELLED)
        OrderStatus.PROCESSING -> listOf(OrderStatus.COMPLETED, OrderStatus.CANCELLED)
        OrderStatus.COMPLETED -> emptyList()
        OrderStatus.CANCELLED -> emptyList()
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        icon = { Icon(Icons.Default.Edit, null) },
        title = { Text("Update Status Pesanan") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Order #${currentOrder.orderNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Status saat ini: ${currentStatus.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (availableStatuses.isEmpty()) {
                    Text(
                        text = "Pesanan ini tidak dapat diupdate lagi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "Pilih status baru:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    availableStatuses.forEach { status ->
                        Button(
                            onClick = { onUpdateStatus(status) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading,
                            colors = when (status) {
                                OrderStatus.CANCELLED -> ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                                OrderStatus.COMPLETED -> ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                                else -> ButtonDefaults.buttonColors()
                            }
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(status.displayName)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            if (!isLoading && availableStatuses.isNotEmpty()) {
                TextButton(onClick = onDismiss) {
                    Text("Batal")
                }
            }
        }
    )
}

// ✅ MOVE TO TOP-LEVEL
@Composable
fun ErrorAdminOrderDetailState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Terjadi Kesalahan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Coba Lagi")
        }
    }
}

// ✅ MOVE TO TOP-LEVEL (helper functions)
private fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}