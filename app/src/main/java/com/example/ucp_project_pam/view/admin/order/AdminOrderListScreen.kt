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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp_project_pam.modeldata.Order
import com.example.ucp_project_pam.modeldata.OrderUiState
import com.example.ucp_project_pam.modeldata.OrderStatus
import com.example.ucp_project_pam.viewmodel.order.AdminOrderViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderListScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (Int) -> Unit,
    viewModel: AdminOrderViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val orderUiState by remember { derivedStateOf { viewModel.orderUiState } }
    val filterStatus by remember { derivedStateOf { viewModel.filterStatus } }

    var showFilterDialog by remember { mutableStateOf(false) }

    // Load orders
    LaunchedEffect(Unit) {
        viewModel.getAllOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    // Filter Button
                    IconButton(onClick = { showFilterDialog = true }) {
                        Badge(
                            containerColor = if (filterStatus != null)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Icon(Icons.Default.Refresh, "Filter")
                        }
                    }

                    // Refresh Button
                    IconButton(onClick = { viewModel.refreshOrders() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
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
            when (orderUiState) {
                is OrderUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is OrderUiState.Success -> {
                    val orders = (orderUiState as OrderUiState.Success).orders

                    if (orders.isEmpty()) {
                        EmptyAdminOrderState(
                            filterActive = filterStatus != null,
                            onClearFilter = { viewModel.clearFilter() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Active Filter Chip
                            if (filterStatus != null) {
                                item {
                                    FilterChip(
                                        selected = true,
                                        onClick = { viewModel.clearFilter() },
                                        label = { Text("Status: ${filterStatus!!.displayName}") },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear filter",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    )
                                }
                            }

                            items(orders, key = { it.id }) { order ->
                                AdminOrderCard(
                                    order = order,
                                    onClick = { onOrderClick(order.id) }
                                )
                            }
                        }
                    }
                }

                is OrderUiState.Error -> {
                    ErrorAdminOrderState(
                        message = (orderUiState as OrderUiState.Error).message,
                        onRetry = { viewModel.getAllOrders() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }
        }
    }

    // Filter Dialog
    if (showFilterDialog) {
        FilterOrderDialog(
            currentFilter = filterStatus,
            onDismiss = { showFilterDialog = false },
            onFilterSelected = { status ->
                viewModel.updateFilterStatus(status)
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun AdminOrderCard(
    order: Order,
    onClick: () -> Unit
) {
    val status = OrderStatus.fromValue(order.status)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${order.orderNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatDate(order.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OrderStatusBadge(status = status)
            }

            Divider()

            // Order Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${order.items.size} item",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatRupiah(order.totalPrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (containerColor, contentColor) = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        OrderStatus.PROCESSING -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        OrderStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
fun FilterOrderDialog(
    currentFilter: OrderStatus?,
    onDismiss: () -> Unit,
    onFilterSelected: (OrderStatus?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Refresh, null) },
        title = { Text("Filter Status") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Orders
                FilterChip(
                    selected = currentFilter == null,
                    onClick = { onFilterSelected(null) },
                    label = { Text("Semua Pesanan") }
                )

                // Status Options
                OrderStatus.values().forEach { status ->
                    FilterChip(
                        selected = currentFilter == status,
                        onClick = { onFilterSelected(status) },
                        label = { Text(status.displayName) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun EmptyAdminOrderState(
    filterActive: Boolean,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (filterActive) "Tidak Ada Pesanan" else "Belum Ada Pesanan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (filterActive)
                "Tidak ada pesanan dengan filter ini"
            else
                "Belum ada pesanan dari customer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (filterActive) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = onClearFilter) {
                Text("Hapus Filter")
            }
        }
    }
}

@Composable
fun ErrorAdminOrderState(
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

private fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}