package com.fios.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fios.app.domain.model.VaultAsset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferToVaultDialog(
    assets: List<VaultAsset>,
    onDismiss: () -> Unit,
    onConfirm: (VaultAsset?, String, Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedAsset by remember { mutableStateOf<VaultAsset?>(null) }
    var newAssetName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("TRANSFERENCIA DE PAQUETES", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Selecciona un activo existente o crea un identificador nuevo para el almacenamiento frío.",
                    style = MaterialTheme.typography.bodySmall
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedAsset?.name ?: "CREAR NUEVO ACTIVO",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Destino de Bóveda") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("[ NUEVO ACTIVO ]") },
                            onClick = {
                                selectedAsset = null
                                expanded = false
                            }
                        )
                        assets.forEach { asset ->
                            DropdownMenuItem(
                                text = { Text(asset.name) },
                                onClick = {
                                    selectedAsset = asset
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedAsset == null) {
                    OutlinedTextField(
                        value = newAssetName,
                        onValueChange = { newAssetName = it },
                        label = { Text("Nombre del Nuevo Activo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto a Transferir") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    if (amountDouble > 0 && (selectedAsset != null || newAssetName.isNotBlank())) {
                        onConfirm(selectedAsset, newAssetName, amountDouble)
                    }
                }
            ) {
                Text("TRANSFERIR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ABORTAR")
            }
        }
    )
}
