package com.fios.app.presentation.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fios.app.domain.model.VaultAsset
import com.fios.app.presentation.commandcenter.formatCurrency
import com.fios.app.ui.components.AddVaultAssetDialog
import com.fios.app.ui.components.ConfirmDeleteDialog
import com.fios.app.ui.components.FinancialCard

@Composable
fun VaultScreen(
    viewModel: VaultViewModel = viewModel(factory = VaultViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var assetToDelete by remember { mutableStateOf<VaultAsset?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Cifrar Activo")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "ALMACENAMIENTO CIFRADO (BÓVEDA)",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Activos fuera de línea. Acceso restringido por protocolo de seguridad.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            FinancialCard(
                title = "VALOR TOTAL EN BÓVEDA",
                amount = formatCurrency(uiState.totalVaultBalance)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ARCHIVOS DE ACTIVOS",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.assets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "LA BÓVEDA ESTÁ VACÍA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.assets) { asset ->
                        VaultAssetItem(
                            asset = asset,
                            onDelete = { assetToDelete = asset }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddVaultAssetDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, amount ->
                    viewModel.addAsset(name, amount)
                    showAddDialog = false
                }
            )
        }

        assetToDelete?.let { asset ->
            ConfirmDeleteDialog(
                itemName = asset.name,
                onDismiss = { assetToDelete = null },
                onConfirm = {
                    viewModel.deleteAsset(asset)
                    assetToDelete = null
                }
            )
        }
    }
}

@Composable
fun VaultAssetItem(
    asset: VaultAsset,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Security, 
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name.uppercase(),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "CIFRADO: ${asset.encryptionLevel}-BIT AES",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = formatCurrency(asset.balance),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete, 
                    contentDescription = "Borrar",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                )
            }
        }
    }
}
