package com.fios.app.data.repository

import com.fios.app.data.local.dao.AchievementDao
import com.fios.app.data.local.entities.AchievementEntity
import com.fios.app.domain.model.Achievement
import com.fios.app.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AchievementRepositoryImpl(
    private val dao: AchievementDao
) : AchievementRepository {

    override fun getAchievements(): Flow<List<Achievement>> {
        return dao.getAllAchievements().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertAchievement(achievement: Achievement) {
        dao.insertAchievement(achievement.toEntity())
    }

    override suspend fun unlockAchievement(title: String) {
        dao.unlockAchievement(title)
    }
}

fun AchievementEntity.toDomain() = Achievement(
    id = id,
    title = title,
    description = description,
    unlocked = unlocked,
    iconCode = iconCode
)

fun Achievement.toEntity() = AchievementEntity(
    id = id,
    title = title,
    description = description,
    unlocked = unlocked,
    iconCode = iconCode
)
