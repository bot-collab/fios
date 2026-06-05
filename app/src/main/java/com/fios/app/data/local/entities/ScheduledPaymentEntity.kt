package com.fios.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un "Cron Job" o pago programado que se autoejecuta o avisa cada mes.
 */
@Entity(tableName = "scheduled_payments")
data class ScheduledPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val dayOfMonth: Int,
    val category: String,
    val type: LogType,
    val lastExecutedMonth: Int, // MM del año para evitar duplicados en el mismo mes
    val lastExecutedYear: Int    // YYYY
)
