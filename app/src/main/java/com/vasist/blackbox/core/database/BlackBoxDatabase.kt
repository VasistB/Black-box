package com.vasist.blackbox.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vasist.blackbox.core.database.dao.ChallengeDao
import com.vasist.blackbox.core.database.dao.RecordDao
import com.vasist.blackbox.core.database.entity.ChallengeEntity
import com.vasist.blackbox.core.database.entity.RecordEntity

@Database(
    entities = [RecordEntity::class, ChallengeEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BlackBoxDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun challengeDao(): ChallengeDao
}
