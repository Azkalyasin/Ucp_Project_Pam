package com.example.ucp_project_pam.view.admin


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenAdmin(
    onProfileClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onMenuClick: () -> Unit,
    onOrderClick: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", color = Color.White) }, // ✅ Text putih
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9800) // ✅ ORANGE
                ),
                actions = {
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
                        text = "Selamat Datang, Admin!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100) // ✅ ORANGE GELAP
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kelola aplikasi UMKM Anda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF57C00) // ✅ ORANGE MEDIUM
                    )
                }
            }

            // Management Section
            Text(
                text = "Manajemen Data",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
                color = Color(0xFF424242) // ✅ GREY GELAP
            )

            // Category Management - BLUE
            AdminMenuCard(
                title = "Kategori Produk",
                description = "Kelola kategori produk UMKM",
                icon = Icons.Default.List,
                onClick = onCategoryClick,
                cardColor = Color(0xFFBBDEFB),
                iconColor = Color(0xFF2196F3)
            )

            // Menu Management - ORANGE
            AdminMenuCard(
                title = "Menu Produk",
                description = "Kelola menu dan produk UMKM",
                icon = Icons.Default.ShoppingCart,
                onClick = onMenuClick,
                cardColor = Color(0xFFBBDEFB),
                iconColor = Color(0xFF2196F3)
            )

            // Order Management - BLUE
            AdminMenuCard(
                title = "Pesanan",
                description = "Kelola pesanan pelanggan",
                icon = Icons.Default.Menu,
                onClick = onOrderClick,
                cardColor = Color(0xFFBBDEFB),
                iconColor = Color(0xFF2196F3),
                enabled = true
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button - RED
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFF44336)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFF44336)
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
fun AdminMenuCard(
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