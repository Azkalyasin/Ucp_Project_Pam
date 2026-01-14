package com.example.ucp_project_pam.view.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ucp_project_pam.modeldata.*
import com.example.ucp_project_pam.view.components.CartBadge
import com.example.ucp_project_pam.view.components.QuantitySelector
import com.example.ucp_project_pam.viewmodel.customer.CustomerMenuDetailViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMenuDetailScreen(
    menuId: Int,
    onNavigateBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: CustomerMenuDetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val detailState by remember { derivedStateOf { viewModel.menuDetailUiState } }
    val addToCartState by remember { derivedStateOf { viewModel.addToCartState } }
    val selectedQuantity by remember { derivedStateOf { viewModel.selectedQuantity } }
    val cartItemCount by remember { derivedStateOf { viewModel.cartItemCount } }

    val snackbarHostState = remember { SnackbarHostState() }

    // Load menu detail
    LaunchedEffect(menuId) {
        viewModel.getMenuById(menuId)
        viewModel.getCartCount()
        viewModel.resetQuantity()
    }

    // Handle add to cart result
    LaunchedEffect(addToCartState) {
        when (addToCartState) {
            is CartMutationUiState.Success -> {
                val message = (addToCartState as CartMutationUiState.Success).message
                snackbarHostState.showSnackbar(message)
                viewModel.resetAddToCartState()
                viewModel.resetQuantity()
                viewModel.getCartCount()
            }
            is CartMutationUiState.Error -> {
                val message = (addToCartState as CartMutationUiState.Error).message
                snackbarHostState.showSnackbar(message)
                viewModel.resetAddToCartState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Menu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    CartBadge(
                        itemCount = cartItemCount,
                        onClick = onCartClick
                    )
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
                is MenuDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is MenuDetailUiState.Success -> {
                    val menu = (detailState as MenuDetailUiState.Success).menu
                    MenuDetailContent(
                        menu = menu,
                        selectedQuantity = selectedQuantity,
                        onIncrement = { viewModel.increaseQuantity(menu.stock) },
                        onDecrement = { viewModel.decreaseQuantity() },
                        onAddToCart = { viewModel.addToCart(menu.id) },
                        isAddingToCart = addToCartState is CartMutationUiState.Loading
                    )
                }

                is MenuDetailUiState.Error -> {
                    ErrorDetailState(
                        message = (detailState as MenuDetailUiState.Error).message,
                        onRetry = { viewModel.getMenuById(menuId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun MenuDetailContent(
    menu: Menu,
    selectedQuantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onAddToCart: () -> Unit,
    isAddingToCart: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Image
            if (menu.imageUrl != null) {
                AsyncImage(
                    model = menu.imageUrl,
                    contentDescription = menu.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.padding(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name & Category
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = menu.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = menu.category.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Availability Badge
                    Surface(
                        color = if (menu.isAvailable)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (menu.isAvailable) "Tersedia" else "Habis",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (menu.isAvailable)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Price
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Harga",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = formatRupiah(menu.price),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Stock Info
                if (menu.stock != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stock Tersedia",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${menu.stock} item",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Description
                if (!menu.description.isNullOrBlank()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Deskripsi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = menu.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Bottom Bar (Add to Cart Section)
        if (menu.isAvailable) {
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
                    // Quantity Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jumlah",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        QuantitySelector(
                            quantity = selectedQuantity,
                            onIncrement = onIncrement,
                            onDecrement = onDecrement,
                            enabled = !isAddingToCart,
                            maxQuantity = menu.stock
                        )
                    }

                    // Add to Cart Button
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isAddingToCart
                    ) {
                        if (isAddingToCart) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tambah ke Keranjang",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = formatRupiah(menu.price * selectedQuantity),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Unavailable Message
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Menu ini sedang tidak tersedia",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ErrorDetailState(
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