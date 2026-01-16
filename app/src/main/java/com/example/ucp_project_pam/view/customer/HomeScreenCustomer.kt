package com.example.ucp_project_pam.view.customer

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
import com.example.ucp_project_pam.view.components.CartBadge
import com.example.ucp_project_pam.viewmodel.customer.CustomerMenuViewModel
import com.example.ucp_project_pam.viewmodel.provider.PenyediaViewModel
import androidx.compose.ui.graphics.Color
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenCustomer(
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: CustomerMenuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val cartItemCount by remember { derivedStateOf { viewModel.cartItemCount } }

    LaunchedEffect(Unit) {
        viewModel.getCartCount()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Dashboard", color = Color.White) }, // ✅ Text putih
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9800) // ✅ ORANGE
                ),
                actions = {
                    CartBadge(
                        itemCount = cartItemCount,
                        onClick = onCartClick
                    )
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White // ✅ Icon putih
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Card - ORANGE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFE0B2) // ✅ ORANGE TERANG
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selamat Datang!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100) // ✅ ORANGE GELAP
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pesan menu favorit Anda sekarang",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF57C00) // ✅ ORANGE MEDIUM
                    )
                }
            }

            // Cart Summary (jika ada item) - BLUE
            if (cartItemCount > 0) {
                Card(
                    onClick = onCartClick,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFBBDEFB) // ✅ BLUE TERANG
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF2196F3) // ✅ BLUE
                            )
                            Column {
                                Text(
                                    text = "Keranjang Belanja",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0D47A1) // ✅ BLUE GELAP
                                )
                                Text(
                                    text = "$cartItemCount item",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1976D2) // ✅ BLUE MEDIUM
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF2196F3) // ✅ BLUE
                        )
                    }
                }
            }

            // Menu Section
            Text(
                text = "Menu",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
                color = Color(0xFF424242) // ✅ GREY GELAP
            )

            // Browse Menu Card - ORANGE
            CustomerMenuCard(
                title = "Lihat Menu",
                description = "Jelajahi menu makanan & minuman",
                icon = Icons.Default.List,
                onClick = onMenuClick,
                cardColor = Color(0xFFBBDEFB), // ✅ BLUE TERANG
                iconColor = Color(0xFF2196F3)  // ✅ ORANGE
            )

            // My Orders Card - BLUE
            CustomerMenuCard(
                title = "Pesanan Saya",
                description = "Lihat riwayat pesanan Anda",
                icon = Icons.Default.ShoppingCart,
                onClick = onOrdersClick,
                cardColor = Color(0xFFBBDEFB), // ✅ BLUE TERANG
                iconColor = Color(0xFF2196F3)  // ✅ BLUE
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button - RED
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFF44336) // ✅ RED
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFF44336) // ✅ RED BORDER
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}

@Composable
fun CustomerMenuCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    cardColor: Color, // ✅ TAMBAH parameter warna
    iconColor: Color, // ✅ TAMBAH parameter warna icon
    enabled: Boolean = true
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp), // ✅ Height fixed 100dp (> 48dp sesuai SRS)
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) cardColor
            else Color(0xFFF5F5F5).copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon dengan background circle
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = if (enabled) iconColor.copy(alpha = 0.2f)
                else Color(0xFF9E9E9E).copy(alpha = 0.2f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (enabled) iconColor
                    else Color(0xFF9E9E9E)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color(0xFF212121) // ✅ Hitam
                    else Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) Color(0xFF757575) // ✅ Grey
                    else Color(0xFFBDBDBD)
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = if (enabled) Color(0xFF9E9E9E)
                else Color(0xFFE0E0E0),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}