package com.yeo.develop.stepcounter.database.steps.reposittory

import androidx.room.Query
import androidx.room.Transaction
import com.yeo.develop.stepcounter.database.steps.StepDao
import com.yeo.develop.stepcounter.database.steps.StepDataEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 원본 데이터 소스를 추상화하여, 뷰모델이 특정 데이터 소스에 의존하지 않게 하기위한 목적을 갖는 repository입니다.
 * */
class StepRepository @Inject constructor(private val stepDao: StepDao) {
    fun getStepsHistory(): Flow<List<StepDataEntity>?> {
        return stepDao.getStepsHistoryListByFlow()
    }

    suspend fun insertOrUpdate(stepDataEntity: StepDataEntity) {
        stepDao.insertOrUpdate(stepDataEntity)
    }
}