package com.fios.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fios.app.data.local.entities.LogType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, LogType, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(LogType.OUTBOUND) }
    var category by remember { mutableStateOf("SUBSISTEMA") }

    val isFormValid = name.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("INYECTAR LOG DE SISTEMA", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Operación") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isBlank() && name.isNotEmpty()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Volumen de Datos (Monto)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = amount.isNotEmpty() && (amount.toDoubleOrNull() ?: 0.0) <= 0
                )
                
                Text("Tipo de Tráfico:", style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = type == LogType.INBOUND,
                        onClick = { type = LogType.INBOUND }
                    )
                    Text("RAM (Entrada)")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = type == LogType.OUTBOUND,
                        onClick = { type = LogType.OUTBOUND }
                    )
                    Text("CPU (Salida)")
                }

                val categories = listOf("CORE", "SUBSISTEMA", "PERIFÉRICOS", "RED")
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sector / Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
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
                enabled = isFormValid,
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    onConfirm(name, amountDouble, type, category)
                }
            ) {
                Text("EJECUTAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ABORTAR")
            }
        }
    )
}
