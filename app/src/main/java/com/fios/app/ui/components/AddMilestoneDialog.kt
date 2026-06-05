package com.fios.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMilestoneDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var priority by remember { mutableFloatStateOf(1f) }
    var sector by remember { mutableStateOf("EMERGENCY_BUFFER") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("CREAR MILESTONE DE PROYECTO", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Proyecto") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Capacidad Objetivo (Monto)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Prioridad: ${priority.toInt()}", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = priority,
                    onValueChange = { priority = it },
                    valueRange = 1f..5f,
                    steps = 3
                )

                val sectors = listOf("EMERGENCY_BUFFER", "HARDWARE_UPGRADE", "SOFTWARE_LICENSES", "CLOUD_STORAGE")
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = sector,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sector de Asignación") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sectors.forEach { sec ->
                            DropdownMenuItem(
                                text = { Text(sec) },
                                onClick = {
                                    sector = sec
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetDouble = target.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && targetDouble > 0) {
                        onConfirm(name, targetDouble, priority.toInt(), sector)
                    }
                }
            ) {
                Text("INICIAR PROYECTO")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR")
            }
        }
    )
}
