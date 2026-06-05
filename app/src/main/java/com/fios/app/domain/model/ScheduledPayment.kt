package com.fios.app.domain.model

import com.fios.app.data.local.entities.LogType

data class ScheduledPayment(
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val dayOfMonth: Int,
    val category: String,
    val type: LogType,
    val lastExecutedMonth: Int,
    val lastExecutedYear: Int
)
