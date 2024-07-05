package com.yeo.develop.stepcounter.database.steps

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yeo.develop.stepcounter.ApplicationConstants

@Entity(tableName = ApplicationConstants.TABLE_NAME)
data class StepDataEntity (
    //날짜를 PK로 설정합니다.
    @PrimaryKey(autoGenerate = false)
    val targetDate: String,
    val steps: Int
)
