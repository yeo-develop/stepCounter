package com.yeo.develop.stepcounter.modules

import com.yeo.develop.stepcounter.database.steps.StepDao
import com.yeo.develop.stepcounter.database.steps.reposittory.StepRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideStepRepository(stepDao: StepDao): StepRepository =
        StepRepository(stepDao)
}