package com.yeo.develop.stepcounter.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StepDataEntity::class], version = 1)
abstract class StepDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
}