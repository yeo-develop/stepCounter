package com.yeo.develop.stepcounter.activities.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeo.develop.stepcounter.database.steps.StepDataEntity
import com.yeo.develop.stepcounter.database.steps.reposittory.StepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkingHistoryViewModel @Inject constructor(
    private val stepRepository: StepRepository
) : ViewModel() {
    private val stepHistory = stepRepository
        .getStepsHistory()
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val totalStepsCounts = stepHistory.map { stepDataList ->
        stepDataList.sumOf { index -> index.steps }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    //통상적으로 1걸음 당 0.04 칼로리가 빠진다고 합니다
    private val totalCaloriesBurned = totalStepsCounts.map {
        it.toDouble() * 0.04
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    private val _uiState = MutableStateFlow(WalkingHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(stepHistory, totalStepsCounts, totalCaloriesBurned) { stepHistory, totalStepCount, totalCalroriesBurned ->
                _uiState.update {
                    it.copy(
                        dailyStepHistory = stepHistory,
                        totalStepCount = totalStepCount,
                        totalCaloriesBurned = totalCalroriesBurned
                    )
                }
            }.collect()
        }
    }
    data class WalkingHistoryUiState(
        val dailyStepHistory: List<StepDataEntity> = emptyList(),
        val totalStepCount: Int = 0,
        val totalCaloriesBurned: Double = 0.0
    )
}