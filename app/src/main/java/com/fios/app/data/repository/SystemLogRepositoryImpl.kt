package com.fios.app.data.repository

import com.fios.app.data.local.dao.SystemLogDao
import com.fios.app.data.local.entities.SystemLogEntity
import com.fios.app.domain.model.SystemLog
import com.fios.app.domain.repository.SystemLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SystemLogRepositoryImpl(
    private val dao: SystemLogDao
) : SystemLogRepository {

    override fun getLogs(): Flow<List<SystemLog>> {
        return dao.getAllLogs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertLog(log: SystemLog) {
        dao.insertLog(log.toEntity())
    }

    override suspend fun deleteLog(log: SystemLog) {
        dao.deleteLog(log.toEntity())
    }

    override fun getTotalInbound(): Flow<Double?> {
        return dao.getTotalInbound()
    }

    override fun getTotalOutbound(): Flow<Double?> {
        return dao.getTotalOutbound()
    }
}

fun SystemLogEntity.toDomain() = SystemLog(
    id = id,
    timestamp = timestamp,
    operationName = operationName,
    dataVolume = dataVolume,
    category = category,
    type = type,
    metadata = metadata
)

fun SystemLog.toEntity() = SystemLogEntity(
    id = id,
    timestamp = timestamp,
    operationName = operationName,
    dataVolume = dataVolume,
    category = category,
    type = type,
    metadata = metadata
)
