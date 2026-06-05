package com.fios.app.domain.repository

import com.fios.app.domain.model.Milestone
import kotlinx.coroutines.flow.Flow

interface MilestoneRepository {
    fun getMilestones(): Flow<List<Milestone>>
    suspend fun insertMilestone(milestone: Milestone)
    suspend fun updateMilestone(milestone: Milestone)
    suspend fun deleteMilestone(milestone: Milestone)
}
