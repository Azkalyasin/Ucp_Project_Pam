package com.example.ucp_project_pam.view.customer

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
import com.example.ucp_project_pam.modeldata.Cart
import com.example.ucp_project_pam.modeldata.CartUiState
import com.example.ucp_project_pam.modeldata.OrderMutationUiState
import com.example.ucp_project_pam.viewmodel.order.CheckoutViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onOrderSuccess: (Int) -> Unit,
    viewModel: CheckoutViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val cartUiState by remember { derivedStateOf { viewModel.cartUiState } }
    val checkoutState by remember { derivedStateOf { viewModel.checkoutState } }
    val addressFormState by remember { derivedStateOf { viewModel.addressFormState } }

    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showItemUnavailableDialog by remember { mutableStateOf(false) } // ✅ TAMBAH
    var errorMessage by remember { mutableStateOf("") } // ✅ TAMBAH

    // Load cart
    LaunchedEffect(Unit) {
        viewModel.getCart()
    }

    // Handle checkout result
    LaunchedEffect(checkoutState) {
        when (checkoutState) {
            is OrderMutationUiState.Success -> {
                val result = checkoutState as OrderMutationUiState.Success
                snackbarHostState.showSnackbar(result.message)

                result.order?.let { order ->
                    onOrderSuccess(order.id)
                }

                viewModel.resetCheckoutState()
            }
            is OrderMutationUiState.Error -> {
                val message = (checkoutState as OrderMutationUiState.Error).message

                // ✅ TAMBAH: Check if error is about item availability (REQ-ORDER-005)
                if (message.contains("tidak tersedia", ignoreCase = true) ||
                    message.contains("habis", ignoreCase = true) ||
                    message.contains("stock", ignoreCase = true) ||
                    message.contains("available", ignoreCase = true)) {
                    errorMessage = message
                    showItemUnavailableDialog = true
                } else {
                    snackbarHostState.showSnackbar(message)
                }

                viewModel.resetCheckoutState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
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
                        EmptyCheckoutState(
                            onNavigateBack = onNavigateBack,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        CheckoutContent(
                            cart = cart,
                            addressFormState = addressFormState,
                            onAddressChange = { viewModel.updateAddress(it) },
                            onCheckout = { showConfirmDialog = true },
                            isLoading = checkoutState is OrderMutationUiState.Loading
                        )
                    }
                }

                is CartUiState.Error -> {
                    ErrorCheckoutState(
                        message = (cartUiState as CartUiState.Error).message,
                        onRetry = { viewModel.getCart() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }
        }

        // ✅ FIX: Dialogs harus di luar Box tapi dalam Scaffold
        // Confirmation Dialog (REQ-ORDER-002)
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = {
                    Text(
                        "Konfirmasi Pesanan",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("Pastikan pesanan Anda sudah benar. Lanjutkan?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            viewModel.checkout()
                        }
                    ) {
                        Text("Ya, Pesan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // ✅ TAMBAH: Item Unavailable Dialog (REQ-ORDER-005)
        if (showItemUnavailableDialog) {
            AlertDialog(
                onDismissRequest = { showItemUnavailableDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(
                        "Item Tidak Tersedia",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(errorMessage.ifEmpty {
                        "Beberapa item di keranjang Anda tidak tersedia atau stok habis. Silakan kembali ke keranjang untuk memperbaruinya."
                    })
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showItemUnavailableDialog = false
                            onNavigateBack() // Go back to cart
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Kembali ke Keranjang")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showItemUnavailableDialog = false }) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

@Composable
fun CheckoutContent(
    cart: Cart,
    addressFormState: com.example.ucp_project_pam.viewmodel.order.AddressFormState,
    onAddressChange: (String) -> Unit,
    onCheckout: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Scrollable Content
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order Items Section
            item {
                Text(
                    text = "Ringkasan Pesanan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(cart.items, key = { it.id }) { item ->
                CheckoutItemCard(item = item)
            }

            // Divider
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // Address Input Section
            item {
                Text(
                    text = "Alamat Pengiriman",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = addressFormState.address,
                    onValueChange = onAddressChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Alamat Lengkap") },
                    placeholder = { Text("Masukkan alamat pengiriman...") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, "Alamat")
                    },
                    isError = addressFormState.addressError != null,
                    supportingText = {
                        addressFormState.addressError?.let { error ->
                            Text(error)
                        }
                    },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Bottom Bar (Total & Checkout Button)
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
                // Total Summary
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
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buat Pesanan",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutItemCard(
    item: com.example.ucp_project_pam.modeldata.CartItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.menu.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatRupiah(item.menu.price),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "×",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Subtotal
            Text(
                text = formatRupiah(item.subtotal),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun EmptyCheckoutState(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Keranjang Kosong",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tidak ada item untuk checkout",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNavigateBack) {
            Text("Kembali")
        }
    }
}

@Composable
fun ErrorCheckoutState(
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