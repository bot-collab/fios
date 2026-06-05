package com.fios.app.domain.repository

import com.fios.app.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun getAchievements(): Flow<List<Achievement>>
    suspend fun insertAchievement(achievement: Achievement)
    suspend fun unlockAchievement(title: String)
}
