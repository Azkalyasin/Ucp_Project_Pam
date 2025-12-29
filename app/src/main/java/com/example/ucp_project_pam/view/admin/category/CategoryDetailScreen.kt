package com.example.ucp_project_pam.view.admin.category


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp_project_pam.modeldata.Category
import com.example.ucp_project_pam.modeldata.CategoryDetailUiState
import com.example.ucp_project_pam.modeldata.CategoryMutationUiState
import com.example.ucp_project_pam.viewmodel.category.CategoryDetailViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: Int,
    onNavigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: CategoryDetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val detailState = viewModel.categoryDetailUiState
    val mutationState by remember { derivedStateOf { viewModel.deleteMutationState } }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Fetch detail
    LaunchedEffect(categoryId) {
        viewModel.getCategoryById(categoryId)
    }

    // Handle delete result
    LaunchedEffect(mutationState) {
        when (mutationState) {
            is CategoryMutationUiState.Success -> {
                snackbarHostState.showSnackbar(
                    (mutationState as CategoryMutationUiState.Success).message
                )
                viewModel.resetDeleteState()
                onNavigateBack()
            }
            is CategoryMutationUiState.Error -> {
                snackbarHostState.showSnackbar(
                    (mutationState as CategoryMutationUiState.Error).message
                )
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Kategori") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(categoryId) }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Hapus")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (detailState) {
                is CategoryDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is CategoryDetailUiState.Success -> {
                    CategoryDetailContent(
                        category = detailState.category
                    )
                }

                is CategoryDetailUiState.Error -> {
                    ErrorState(
                        message = detailState.message,
                        onRetry = { viewModel.getCategoryById(categoryId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {}
            }

            if (mutationState is CategoryMutationUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Kategori") },
            text = { Text("Yakin ingin menghapus kategori ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(categoryId)
                        showDeleteDialog = false
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
}

/* ====================== CONTENT ====================== */

@Composable
fun CategoryDetailContent(category: Category) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        DetailItem("ID Kategori", category.id.toString())
        DetailItem("Deskripsi", category.description ?: "-")
        DetailItem("Dibuat", formatDateTime(category.createdAt))
        DetailItem("Diupdate", formatDateTime(category.updatedAt))
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


/* ====================== HELPER ====================== */

private fun formatDateTime(dateTime: String): String {
    return try {
        val input = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.US
        ).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val output = SimpleDateFormat(
            "dd MMM yyyy HH:mm",
            Locale("id", "ID")
        )

        output.format(input.parse(dateTime)!!)
    } catch (e: Exception) {
        dateTime
    }
}
