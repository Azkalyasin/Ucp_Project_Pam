package com.example.ucp_project_pam.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CartBadge(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Box {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Keranjang"
            )

            if (itemCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = if (itemCount > 99) "99+" else itemCount.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}