package com.vasist.blackbox.core.di

import com.vasist.blackbox.data.repository.ChallengeRepositoryImpl
import com.vasist.blackbox.data.repository.RecordRepositoryImpl
import com.vasist.blackbox.domain.repository.ChallengeRepository
import com.vasist.blackbox.domain.repository.RecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecordRepository(
        recordRepositoryImpl: RecordRepositoryImpl
    ): RecordRepository

    @Binds
    @Singleton
    abstract fun bindChallengeRepository(
        challengeRepositoryImpl: ChallengeRepositoryImpl
    ): ChallengeRepository
}
