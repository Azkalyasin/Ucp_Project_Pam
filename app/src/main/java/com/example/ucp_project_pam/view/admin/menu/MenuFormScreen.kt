package com.example.ucp_project_pam.view.admin.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp_project_pam.modeldata.CategoryUiState
import com.example.ucp_project_pam.modeldata.MenuDetailUiState
import com.example.ucp_project_pam.modeldata.MenuMutationUiState
import com.example.ucp_project_pam.view.components.ImagePickerField
import com.example.ucp_project_pam.viewmodel.category.CategoryFormViewModel
import com.example.ucp_project_pam.viewmodel.menu.MenuFormViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import com.example.ucp_project_pam.viewmodel.category.CategoryListViewModel
import com.example.ucp_project_pam.viewmodel.menu.MenuFormState
import com.example.ucp_project_pam.viewmodel.menu.validate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuFormScreen(
    menuId: Int? = null, // null untuk create, ada value untuk edit
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    menuViewModel: MenuFormViewModel = viewModel(factory = PenyediaViewModel.Factory),
    categoryViewModel: CategoryListViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val isEditMode = menuId != null && menuId > 0

    val formState by remember { derivedStateOf { menuViewModel.menuFormState } }
    val mutationState by remember { derivedStateOf { menuViewModel.menuMutationUiState } }
    val detailState by remember { derivedStateOf { menuViewModel.menuDetailUiState } }
    val categoryState by remember { derivedStateOf { categoryViewModel.categoryUiState } }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var showCategoryDialog by remember { mutableStateOf(false) }

    // Load data
    LaunchedEffect(menuId) {
        categoryViewModel.getAllCategories()
        if (isEditMode && menuId != null) {
            menuViewModel.getMenuById(menuId)
        } else {
            menuViewModel.resetForm()
        }
    }

    // Handle mutation result
    LaunchedEffect(mutationState) {
        when (mutationState) {
            is MenuMutationUiState.Success -> {
                val message = (mutationState as MenuMutationUiState.Success).message
                snackbarHostState.showSnackbar(message)
                menuViewModel.resetMutationState()
                menuViewModel.resetForm()
                onSuccess()
            }
            is MenuMutationUiState.Error -> {
                val message = (mutationState as MenuMutationUiState.Error).message
                snackbarHostState.showSnackbar(message)
                menuViewModel.resetMutationState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditMode) "Edit Menu" else "Tambah Menu")
                },
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
            // Loading state untuk fetch detail (edit mode)
            if (isEditMode && detailState is MenuDetailUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Info Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (isEditMode) "Edit Menu" else "Tambah Menu Baru",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Isi form di bawah untuk ${if (isEditMode) "mengubah" else "menambahkan"} menu",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Image Picker
                    ImagePickerField(
                        imageUri = formState.imageUri,
                        onImageSelected = { uri ->
                            menuViewModel.updateImageUri(uri)
                        },
                        label = "Foto Menu (Opsional)",
                        enabled = mutationState !is MenuMutationUiState.Loading
                    )

                    // Nama Menu Field
                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = { menuViewModel.updateName(it) },
                        label = { Text("Nama Menu *") },
                        placeholder = { Text("Contoh: Nasi Goreng Spesial") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.nameError != null,
                        supportingText = {
                            if (formState.nameError != null) {
                                Text(
                                    text = formState.nameError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text("Nama menu yang akan ditampilkan")
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        enabled = mutationState !is MenuMutationUiState.Loading
                    )

                    // Deskripsi Field
                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = { menuViewModel.updateDescription(it) },
                        label = { Text("Deskripsi (Opsional)") },
                        placeholder = { Text("Contoh: Nasi goreng dengan telur dan ayam") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        maxLines = 4,
                        enabled = mutationState !is MenuMutationUiState.Loading
                    )

                    // Harga Field
                    OutlinedTextField(
                        value = formState.price,
                        onValueChange = { menuViewModel.updatePrice(it) },
                        label = { Text("Harga *") },
                        placeholder = { Text("Contoh: 25000") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.priceError != null,
                        supportingText = {
                            if (formState.priceError != null) {
                                Text(
                                    text = formState.priceError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text("Harga dalam rupiah (tanpa titik/koma)")
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        enabled = mutationState !is MenuMutationUiState.Loading
                    )

                    // Category Selector
                    OutlinedTextField(
                        value = formState.categoryName,
                        onValueChange = {},
                        label = { Text("Kategori *") },
                        placeholder = { Text("Pilih kategori") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        isError = formState.categoryError != null,
                        supportingText = {
                            if (formState.categoryError != null) {
                                Text(
                                    text = formState.categoryError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { showCategoryDialog = true }) {
                                Icon(Icons.Default.ArrowDropDown, "Pilih kategori")
                            }
                        },
                        enabled = mutationState !is MenuMutationUiState.Loading
                    )

                    // Stock Field
                    OutlinedTextField(
                        value = formState.stock,
                        onValueChange = { menuViewModel.updateStock(it) },
                        label = { Text("Stock (Opsional)") },
                        placeholder = { Text("Contoh: 50") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.stockError != null,
                        supportingText = {
                            if (formState.stockError != null) {
                                Text(
                                    text = formState.stockError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text("Jumlah stock yang tersedia")
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        singleLine = true,
                        enabled = mutationState !is MenuMutationUiState.Loading
                    )

                    // Availability Switch
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Status Ketersediaan",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (formState.isAvailable) "Menu tersedia untuk dipesan"
                                    else "Menu tidak tersedia",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = formState.isAvailable,
                                onCheckedChange = { menuViewModel.updateIsAvailable(it) },
                                enabled = mutationState !is MenuMutationUiState.Loading
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f),
                            enabled = mutationState !is MenuMutationUiState.Loading
                        ) {
                            Text("Batal")
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                if (isEditMode) {
                                    menuViewModel.updateMenu(context)
                                } else {
                                    menuViewModel.createMenu(context)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = mutationState !is MenuMutationUiState.Loading
                        ) {
                            if (mutationState is MenuMutationUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(if (isEditMode) "Simpan Perubahan" else "Tambah Menu")
                        }
                    }

                    // Helper Text
                    Text(
                        text = "* Field wajib diisi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Category Selection Dialog
    if (showCategoryDialog) {
        CategorySelectionDialog(
            categoryState = categoryState,
            onDismiss = { showCategoryDialog = false },
            onCategorySelected = { categoryId, categoryName ->
                menuViewModel.updateCategory(categoryId, categoryName)
                showCategoryDialog = false
            },
            onRetry = { categoryViewModel.getAllCategories() }
        )
    }
}

@Composable
fun CategorySelectionDialog(
    categoryState: CategoryUiState,
    onDismiss: () -> Unit,
    onCategorySelected: (Int, String) -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Kategori") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                when (categoryState) {
                    is CategoryUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize()
                        )
                    }
                    is CategoryUiState.Success -> {
                        val categories = categoryState.categories

                        if (categories.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Belum ada kategori")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tambahkan kategori terlebih dahulu",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { category ->
                                    Card(
                                        onClick = {
                                            onCategorySelected(category.id, category.name)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = category.name,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            if (!category.description.isNullOrBlank()) {
                                                Text(
                                                    text = category.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is CategoryUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = categoryState.message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = onRetry) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}