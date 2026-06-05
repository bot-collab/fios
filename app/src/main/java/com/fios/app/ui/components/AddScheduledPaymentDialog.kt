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
fun AddScheduledPaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Int, LogType, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("1") }
    var type by remember { mutableStateOf(LogType.OUTBOUND) }
    var category by remember { mutableStateOf("CORE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("PROGRAMAR CRON JOB (PAGO)", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Identificador de Tarea") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Volumen de Datos (Monto)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = day,
                    onValueChange = { day = it },
                    label = { Text("Día de Ejecución (1-28)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Tipo de Tráfico:", style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == LogType.INBOUND, onClick = { type = LogType.INBOUND })
                    Text("RAM")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = type == LogType.OUTBOUND, onClick = { type = LogType.OUTBOUND })
                    Text("CPU")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountD = amount.toDoubleOrNull() ?: 0.0
                    val dayI = day.toIntOrNull() ?: 1
                    if (name.isNotBlank() && amountD > 0) {
                        onConfirm(name, amountD, dayI, type, category)
                    }
                }
            ) {
                Text("INSTALAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR")
            }
        }
    )
}
