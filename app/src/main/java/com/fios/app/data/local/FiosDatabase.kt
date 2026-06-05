package com.fios.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fios.app.data.local.dao.AchievementDao
import com.fios.app.data.local.dao.MilestoneDao
import com.fios.app.data.local.dao.ScheduledPaymentDao
import com.fios.app.data.local.dao.SystemLogDao
import com.fios.app.data.local.dao.VaultDao
import com.fios.app.data.local.entities.AchievementEntity
import com.fios.app.data.local.entities.MilestoneEntity
import com.fios.app.data.local.entities.ScheduledPaymentEntity
import com.fios.app.data.local.entities.SystemLogEntity
import com.fios.app.data.local.entities.VaultEntity

@Database(
    entities = [
        SystemLogEntity::class, 
        MilestoneEntity::class, 
        VaultEntity::class, 
        ScheduledPaymentEntity::class,
        AchievementEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FiosDatabase : RoomDatabase() {
    abstract fun systemLogDao(): SystemLogDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun vaultDao(): VaultDao
    abstract fun scheduledPaymentDao(): ScheduledPaymentDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val DATABASE_NAME = "fios_db"
    }
}
