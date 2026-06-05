package com.fios.app.data.repository

import com.fios.app.data.local.dao.MilestoneDao
import com.fios.app.data.local.entities.MilestoneEntity
import com.fios.app.domain.model.Milestone
import com.fios.app.domain.repository.MilestoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MilestoneRepositoryImpl(
    private val dao: MilestoneDao
) : MilestoneRepository {

    override fun getMilestones(): Flow<List<Milestone>> {
        return dao.getAllMilestones().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertMilestone(milestone: Milestone) {
        dao.insertMilestone(milestone.toEntity())
    }

    override suspend fun updateMilestone(milestone: Milestone) {
        dao.updateMilestone(milestone.toEntity())
    }

    override suspend fun deleteMilestone(milestone: Milestone) {
        dao.deleteMilestone(milestone.toEntity())
    }
}

fun MilestoneEntity.toDomain() = Milestone(
    id = id,
    projectName = projectName,
    targetCapacity = targetCapacity,
    currentCapacity = currentCapacity,
    priority = priority,
    deadline = deadline,
    isCompleted = isCompleted,
    sector = sector
)

fun Milestone.toEntity() = MilestoneEntity(
    id = id,
    projectName = projectName,
    targetCapacity = targetCapacity,
    currentCapacity = currentCapacity,
    priority = priority,
    deadline = deadline,
    isCompleted = isCompleted,
    sector = sector
)
