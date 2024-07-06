package com.yeo.develop.stepcounter.modules

import android.content.Context
import com.yeo.develop.stepcounter.datastore.AppDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    fun provideAppDataStore(@ApplicationContext context: Context): AppDataStore =
        AppDataStore(context)
}