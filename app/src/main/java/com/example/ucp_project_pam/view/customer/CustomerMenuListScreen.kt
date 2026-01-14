package com.example.ucp_project_pam.view.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ucp_project_pam.modeldata.*
import com.example.ucp_project_pam.view.components.CartBadge
import com.example.ucp_project_pam.viewmodel.customer.CustomerMenuViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMenuListScreen(
    onNavigateBack: () -> Unit,
    onMenuClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    viewModel: CustomerMenuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val menuUiState by remember { derivedStateOf { viewModel.menuUiState } }
    val addToCartState by remember { derivedStateOf { viewModel.addToCartState } }
    val cartItemCount by remember { derivedStateOf { viewModel.cartItemCount } }
    val filterState by remember { derivedStateOf { viewModel.filterState } }

    val snackbarHostState = remember { SnackbarHostState() }

    // Load menus on first composition
    LaunchedEffect(Unit) {
        viewModel.getAllMenus()
        viewModel.getCartCount()
    }

    // Handle add to cart result
    LaunchedEffect(addToCartState) {
        when (addToCartState) {
            is CartMutationUiState.Success -> {
                val message = (addToCartState as CartMutationUiState.Success).message
                snackbarHostState.showSnackbar(message)
                viewModel.resetAddToCartState()
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
                title = { Text("Menu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    // Cart Badge
                    CartBadge(
                        itemCount = cartItemCount,
                        onClick = onCartClick
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = filterState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari menu...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, "Search")
                },
                trailingIcon = {
                    if (filterState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (menuUiState) {
                    is MenuUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is MenuUiState.Success -> {
                        val menus = (menuUiState as MenuUiState.Success).menus
                            .filter {
                                filterState.searchQuery.isEmpty() ||
                                        it.name.contains(filterState.searchQuery, ignoreCase = true)
                            }

                        if (menus.isEmpty()) {
                            EmptyMenuState(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(menus, key = { it.id }) { menu ->
                                    MenuGridItem(
                                        menu = menu,
                                        onClick = { onMenuClick(menu.id) },
                                        onAddToCart = { viewModel.addToCart(menu.id, 1) },
                                        isLoading = addToCartState is CartMutationUiState.Loading
                                    )
                                }
                            }
                        }
                    }

                    is MenuUiState.Error -> {
                        ErrorMenuState(
                            message = (menuUiState as MenuUiState.Error).message,
                            onRetry = { viewModel.getAllMenus() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun MenuGridItem(
    menu: Menu,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isLoading: Boolean
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image
            Box(modifier = Modifier.fillMaxWidth()) {
                if (menu.imageUrl != null) {
                    AsyncImage(
                        model = menu.imageUrl,
                        contentDescription = menu.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.padding(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Availability Badge
                if (!menu.isAvailable) {
                    Surface(
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Habis",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = menu.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                Text(
                    text = formatRupiah(menu.price),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Add to Cart Button
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = menu.isAvailable && !isLoading,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyMenuState(modifier: Modifier = Modifier) {
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
            text = "Menu tidak ditemukan",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorMenuState(
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
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Coba Lagi")
        }
    }
}

private fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}