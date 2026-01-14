package com.example.ucp_project_pam.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minQuantity: Int = 1,
    maxQuantity: Int? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease Button
        IconButton(
            onClick = onDecrement,
            enabled = enabled && quantity > minQuantity,
            modifier = Modifier.size(36.dp)
        ) {
            Surface(
                color = if (quantity > minQuantity)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Kurangi",
                    modifier = Modifier.padding(8.dp),
                    tint = if (quantity > minQuantity)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }

        // Quantity Display
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.widthIn(min = 48.dp)
        ) {
            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Increase Button
        IconButton(
            onClick = onIncrement,
            enabled = enabled && (maxQuantity == null || quantity < maxQuantity),
            modifier = Modifier.size(36.dp)
        ) {
            Surface(
                color = if (maxQuantity == null || quantity < maxQuantity)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah",
                    modifier = Modifier.padding(8.dp),
                    tint = if (maxQuantity == null || quantity < maxQuantity)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}