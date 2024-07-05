package com.yeo.develop.stepcounter.modules

import android.content.Context
import androidx.room.Room
import com.yeo.develop.stepcounter.ApplicationConstants
import com.yeo.develop.stepcounter.database.steps.StepDao
import com.yeo.develop.stepcounter.database.steps.StepDatabase
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
    fun provideDailyStepDatabase(@ApplicationContext context: Context): StepDatabase =
        Room.databaseBuilder(
            context,
            StepDatabase::class.java,
            ApplicationConstants.DATABASE_NAME
        ).build()

    @Provides
    fun provideStepDao(database: StepDatabase): StepDao =
        database.stepDao()
}