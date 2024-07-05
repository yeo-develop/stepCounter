package com.yeo.develop.stepcounter.database.steps

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow


@Dao
interface StepDao {

    /*
    * 날짜가 바뀔경우, 신규설치 등의 사유로 데이터를 불러올 수 없을때, 엔티티를 신규 생성합니다.
    **/
    @Insert
    suspend fun createDailyStepData(stepDataEntity: StepDataEntity)

    /*
    * Activity등의 화면에서 실시간으로 변경을 감지하여 뷰를 업데이트 해줘야 하기에 flow로 관리합니다.
    * */
    @Query("SELECT steps FROM daily_step_data WHERE targetDate = :date")
    fun getStepsByDateFlow(date: String): Flow<Int?>

    @Query("SELECT steps FROM daily_step_data WHERE targetDate = :date")
    suspend fun getStepsByDate(date: String): Int?

    /**
     * 날짜가 바뀔경우, 신규설치 등의 사유로 데이터를 불러올 수 없을때를 대비합니다.
     **/
    @Transaction
    suspend fun insertOrUpdate(stepDataEntity: StepDataEntity) {
        if (getStepsByDate(stepDataEntity.targetDate) == null) {
            createDailyStepData(stepDataEntity)
        } else {
            updateStepData(
                targetDate = stepDataEntity.targetDate,
                steps = stepDataEntity.steps
            )
        }
    }

    /*
    * 걸음수 변경 감지 시, 걸음 수 update를 위한 함수입니다.
    * */
    @Query("UPDATE daily_step_data SET steps = :steps WHERE targetDate = :targetDate")
    suspend fun updateStepData(targetDate: String, steps: Int)
}