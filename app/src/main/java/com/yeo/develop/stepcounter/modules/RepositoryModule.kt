package com.yeo.develop.stepcounter.modules

import com.yeo.develop.stepcounter.database.steps.StepDao
import com.yeo.develop.stepcounter.database.steps.reposittory.StepRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideStepRepository(stepDao: StepDao): StepRepository =
        StepRepository(stepDao)
}