package com.vasist.blackbox.core.di

import android.content.Context
import androidx.room.Room
import com.vasist.blackbox.core.database.BlackBoxDatabase
import com.vasist.blackbox.core.database.dao.ChallengeDao
import com.vasist.blackbox.core.database.dao.RecordDao
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
    fun provideBlackBoxDatabase(
        @ApplicationContext context: Context,
    ): BlackBoxDatabase {
        return Room.databaseBuilder(
            context,
            BlackBoxDatabase::class.java,
            "blackbox_database",
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideRecordDao(database: BlackBoxDatabase): RecordDao {
        return database.recordDao()
    }

    @Provides
    fun provideChallengeDao(database: BlackBoxDatabase): ChallengeDao {
        return database.challengeDao()
    }
}
