package com.example.ucp_project_pam.view.admin.menu

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ucp_project_pam.modeldata.*
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.example.ucp_project_pam.viewmodel.menu.MenuListViewModel // ✅ Update
import com.example.ucp_project_pam.viewmodel.menu.MenuFilterState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuListScreen(
    onNavigateBack: () -> Unit,
    onAddMenu: () -> Unit,
    onItemClick: (Int) -> Unit,
    onEditMenu: (Int) -> Unit,
    viewModel: MenuListViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val menuUiState by remember { derivedStateOf { viewModel.menuUiState } }
    val mutationState by remember { derivedStateOf { viewModel.deleteMutationState } }
    val filterState by remember { derivedStateOf { viewModel.filterState } }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var menuToDelete by remember { mutableStateOf<Menu?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Load menus on first composition
    LaunchedEffect(Unit) {
        viewModel.getAllMenus()
    }

    // Handle delete result
    LaunchedEffect(mutationState) {
        when (mutationState) {
            is MenuMutationUiState.Success -> {
                viewModel.getAllMenus() // Refresh list
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Menu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    // Filter Button
                    IconButton(onClick = { showFilterDialog = true }) {
                        Badge(
                            containerColor = if (filterState.categoryId != null ||
                                filterState.isAvailable != null)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Icon(Icons.Default.Search, "Filter")
                        }
                    }

                    // Refresh Button
                    IconButton(onClick = { viewModel.getAllMenus() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMenu,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Tambah Menu")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                query = filterState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSearch = { viewModel.getAllMenus() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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

                        if (menus.isEmpty()) {
                            EmptyMenuState(
                                modifier = Modifier.align(Alignment.Center),
                                onAddClick = onAddMenu
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(menus, key = { it.id }) { menu ->
                                    MenuItemCard(
                                        menu = menu,
                                        onClick = { onItemClick(menu.id) },
                                        onEdit = { onEditMenu(menu.id) },
                                        onDelete = {
                                            menuToDelete = menu
                                            showDeleteDialog = true
                                        }
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

    // Delete Confirmation Dialog
    if (showDeleteDialog && menuToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Hapus Menu") },
            text = {
                Text("Apakah Anda yakin ingin menghapus menu \"${menuToDelete?.name}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        menuToDelete?.let { viewModel.deleteMenu(it.id) }
                        showDeleteDialog = false
                        menuToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Filter Dialog
    if (showFilterDialog) {
        FilterMenuDialog(
            currentFilter = filterState,
            onDismiss = { showFilterDialog = false },
            onApply = { newFilter ->
                viewModel.updateFilter(newFilter)
                showFilterDialog = false
            },
            onClear = {
                viewModel.clearFilter()
                showFilterDialog = false
            }
        )
    }

    // Show delete loading
    when (mutationState) {
        is MenuMutationUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {}
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Cari menu...") },
        leadingIcon = {
            Icon(Icons.Default.Search, "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),

        // 🔥 INI KUNCINYA
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch() // 🔥 panggil ViewModel.getAllMenus()
            }
        )
    )
}

@Composable
fun MenuItemCard(
    menu: Menu,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image
            if (menu.imageUrl != null) {
                AsyncImage(
                    model = menu.imageUrl,
                    contentDescription = menu.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Name
                Text(
                    text = menu.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Category
                Text(
                    text = menu.category.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Price
                Text(
                    text = formatRupiah(menu.price),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                // Status & Stock
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Availability Badge
                    Surface(
                        color = if (menu.isAvailable)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (menu.isAvailable) "Tersedia" else "Habis",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (menu.isAvailable)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    // Stock
                    if (menu.stock != null) {
                        Text(
                            text = "Stock: ${menu.stock}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FilterMenuDialog(
    currentFilter: MenuFilterState,
    onDismiss: () -> Unit,
    onApply: (MenuFilterState) -> Unit,
    onClear: () -> Unit
) {
    var tempFilter by remember { mutableStateOf(currentFilter) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Menu") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Availability Filter
                Text(
                    text = "Status Ketersediaan",
                    style = MaterialTheme.typography.titleSmall
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = tempFilter.isAvailable == null,
                        onClick = { tempFilter = tempFilter.copy(isAvailable = null) },
                        label = { Text("Semua") }
                    )
                    FilterChip(
                        selected = tempFilter.isAvailable == true,
                        onClick = { tempFilter = tempFilter.copy(isAvailable = true) },
                        label = { Text("Tersedia") }
                    )
                    FilterChip(
                        selected = tempFilter.isAvailable == false,
                        onClick = { tempFilter = tempFilter.copy(isAvailable = false) },
                        label = { Text("Habis") }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(tempFilter) }) {
                Text("Terapkan")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onClear) {
                    Text("Reset")
                }
                TextButton(onClick = onDismiss) {
                    Text("Batal")
                }
            }
        }
    )
}

@Composable
fun EmptyMenuState(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Belum Ada Menu",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tambahkan menu pertama Anda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onAddClick) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tambah Menu")
        }
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
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Terjadi Kesalahan",
            style = MaterialTheme.typography.titleLarge,
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

// Helper function untuk format rupiah
fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}