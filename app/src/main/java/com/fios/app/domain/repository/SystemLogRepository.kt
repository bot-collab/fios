package com.fios.app.domain.repository

import com.fios.app.domain.model.SystemLog
import kotlinx.coroutines.flow.Flow

interface SystemLogRepository {
    fun getLogs(): Flow<List<SystemLog>>
    suspend fun insertLog(log: SystemLog)
    suspend fun deleteLog(log: SystemLog)
    fun getTotalInbound(): Flow<Double?>
    fun getTotalOutbound(): Flow<Double?>
}
