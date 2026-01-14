package com.example.ucp_project_pam.view.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp_project_pam.modeldata.Cart
import com.example.ucp_project_pam.modeldata.CartUiState
import com.example.ucp_project_pam.modeldata.CartMutationUiState
import com.example.ucp_project_pam.view.components.CartItemCard
import com.example.ucp_project_pam.view.components.EmptyCartState
import com.example.ucp_project_pam.viewmodel.cart.CartViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onBrowseMenu: () -> Unit,
    onCheckout: () -> Unit,
    viewModel: CartViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val cartUiState by remember { derivedStateOf { viewModel.cartUiState } }
    val mutationState by remember { derivedStateOf { viewModel.mutationState } }

    var showClearCartDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load cart
    LaunchedEffect(Unit) {
        viewModel.getMyCart()
    }

    // Handle mutation result
    LaunchedEffect(mutationState) {
        when (mutationState) {
            is CartMutationUiState.Success -> {
                val message = (mutationState as CartMutationUiState.Success).message
                snackbarHostState.showSnackbar(message)
                viewModel.resetMutationState()
            }
            is CartMutationUiState.Error -> {
                val message = (mutationState as CartMutationUiState.Error).message
                snackbarHostState.showSnackbar(message)
                viewModel.resetMutationState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Keranjang Belanja") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    // Clear Cart Button
                    if (cartUiState is CartUiState.Success) {
                        val cart = (cartUiState as CartUiState.Success).cart
                        if (cart.items.isNotEmpty()) {
                            IconButton(onClick = { showClearCartDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Kosongkan Keranjang",
                                    tint = MaterialTheme.colorScheme.error
                                )
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
            when (cartUiState) {
                is CartUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is CartUiState.Success -> {
                    val cart = (cartUiState as CartUiState.Success).cart

                    if (cart.items.isEmpty()) {
                        EmptyCartState(
                            onBrowseMenuClick = onBrowseMenu
                        )
                    } else {
                        CartContent(
                            cart = cart,
                            onIncrement = { menuId, currentQty ->
                                viewModel.increaseQuantity(menuId, currentQty)
                            },
                            onDecrement = { menuId, currentQty ->
                                viewModel.decreaseQuantity(menuId, currentQty)
                            },
                            onRemove = { menuId ->
                                viewModel.removeItem(menuId)
                            },
                            onCheckout = onCheckout,
                            isLoading = mutationState is CartMutationUiState.Loading
                        )
                    }
                }

                is CartUiState.Error -> {
                    ErrorCartState(
                        message = (cartUiState as CartUiState.Error).message,
                        onRetry = { viewModel.getMyCart() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }
        }
    }

    // Clear Cart Confirmation Dialog
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Kosongkan Keranjang") },
            text = {
                Text("Apakah Anda yakin ingin menghapus semua item dari keranjang?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCart()
                        showClearCartDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Kosongkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCartDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun CartContent(
    cart: Cart,
    onIncrement: (Int, Int) -> Unit,
    onDecrement: (Int, Int) -> Unit,
    onRemove: (Int) -> Unit,
    onCheckout: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cart Items List
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cart.items, key = { it.id }) { cartItem ->
                CartItemCard(
                    cartItem = cartItem,
                    onIncrement = {
                        onIncrement(cartItem.menu.id, cartItem.quantity)
                    },
                    onDecrement = {
                        onDecrement(cartItem.menu.id, cartItem.quantity)
                    },
                    onRemove = {
                        onRemove(cartItem.menu.id)
                    },
                    isLoading = isLoading
                )
            }
        }

        // Bottom Summary & Checkout
        Surface(
            shadowElevation = 8.dp,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Summary Card
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
                            text = "Ringkasan Belanja",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Item",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${cart.totalQuantity} item",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Harga",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatRupiah(cart.totalPrice),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Checkout Button
                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorCartState(
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