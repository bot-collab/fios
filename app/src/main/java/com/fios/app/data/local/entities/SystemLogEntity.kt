package com.fios.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a financial transaction in the "OS" metaphor.
 * Transactions are viewed as "Data Transfers" or "System Logs".
 */
@Entity(tableName = "system_logs")
data class SystemLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val operationName: String, // e.g., "Grocery Sync", "Salary Upload"
    val dataVolume: Double,    // The amount of currency
    val category: String,      // e.g., "CORE", "SUBSYSTEM", "PERIPHERALS"
    val type: LogType,         // INBOUND (Income), OUTBOUND (Expense)
    val metadata: String? = null // Additional notes
)

enum class LogType {
    INBOUND,  // Revenue / RAM Increase
    OUTBOUND  // Expense / CPU Load
}
