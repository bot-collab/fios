package com.fios.app.domain.model

import com.fios.app.data.local.entities.LogType

data class SystemLog(
    val id: Long = 0,
    val timestamp: Long,
    val operationName: String,
    val dataVolume: Double,
    val category: String,
    val type: LogType,
    val metadata: String? = null
)
