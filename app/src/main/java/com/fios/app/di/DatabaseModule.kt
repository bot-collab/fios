package com.fios.app.di

import android.content.Context
import androidx.room.Room
import com.fios.app.data.local.FiosDatabase
import com.fios.app.data.local.dao.MilestoneDao
import com.fios.app.data.local.dao.SystemLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FiosDatabase {
        return Room.databaseBuilder(
            context,
            FiosDatabase::class.java,
            FiosDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideSystemLogDao(database: FiosDatabase): SystemLogDao {
        return database.systemLogDao()
    }

    @Provides
    fun provideMilestoneDao(database: FiosDatabase): MilestoneDao {
        return database.milestoneDao()
    }
}
