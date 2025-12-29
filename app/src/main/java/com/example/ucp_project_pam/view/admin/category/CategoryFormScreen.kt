package com.example.ucp_project_pam.view.admin.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp_project_pam.modeldata.CategoryDetailUiState
import com.example.ucp_project_pam.modeldata.CategoryMutationUiState
import com.example.ucp_project_pam.viewmodel.category.CategoryDetailViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import com.example.ucp_project_pam.viewmodel.category.CategoryFormViewModel // ✅ Update import
import com.example.ucp_project_pam.viewmodel.category.CategoryFormState // ✅ Tambah import
import com.example.ucp_project_pam.viewmodel.category.validate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    categoryId: Int? = null, // null untuk create, ada value untuk edit
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CategoryFormViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val isEditMode = categoryId != null && categoryId > 0
    val formState by remember { derivedStateOf { viewModel.categoryFormState } }
    val mutationState by remember { derivedStateOf { viewModel.categoryMutationUiState } }
    val detailState by remember { derivedStateOf { viewModel.categoryDetailUiState } }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // Load category untuk edit
    LaunchedEffect(categoryId) {
        if (categoryId != null && categoryId > 0) {
            viewModel.getCategoryById(categoryId)
        } else {
            viewModel.resetForm()
        }
    }

    // Handle mutation result (create/update)
    LaunchedEffect(mutationState) {
        when (mutationState) {
            is CategoryMutationUiState.Success -> {
                val message = (mutationState as CategoryMutationUiState.Success).message
                snackbarHostState.showSnackbar(message)
                viewModel.resetMutationState()
                viewModel.resetForm()
                onSuccess()
            }
            is CategoryMutationUiState.Error -> {
                val message = (mutationState as CategoryMutationUiState.Error).message
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
                title = {
                    Text(if (isEditMode) "Edit Kategori" else "Tambah Kategori")
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
            if (isEditMode && detailState is CategoryDetailUiState.Loading) {
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
                                text = if (isEditMode) "Edit Kategori" else "Tambah Kategori Baru",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Isi form di bawah untuk ${if (isEditMode) "mengubah" else "menambahkan"} kategori",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Nama Kategori Field
                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Nama Kategori *") },
                        placeholder = { Text("Contoh: Makanan & Minuman") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.nameError != null,
                        supportingText = {
                            if (formState.nameError != null) {
                                Text(
                                    text = formState.nameError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text("Nama kategori produk")
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true
                    )

                    // Deskripsi Field
                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = { Text("Deskripsi (Opsional)") },
                        placeholder = { Text("Contoh: Kategori untuk produk makanan dan minuman") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        maxLines = 4
                    )

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
                            enabled = mutationState !is CategoryMutationUiState.Loading
                        ) {
                            Text("Batal")
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                if (isEditMode) {
                                    viewModel.updateCategory()
                                } else {
                                    viewModel.createCategory()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = mutationState !is CategoryMutationUiState.Loading
                        ) {
                            if (mutationState is CategoryMutationUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(if (isEditMode) "Simpan Perubahan" else "Tambah Kategori")
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
}