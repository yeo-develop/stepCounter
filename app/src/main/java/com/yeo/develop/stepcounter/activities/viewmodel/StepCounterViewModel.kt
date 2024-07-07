package com.yeo.develop.stepcounter.activities.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeo.develop.stepcounter.ApplicationConstants
import com.yeo.develop.stepcounter.datastore.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StepCounterViewModel @Inject constructor(
    private val appDataStore: AppDataStore
) : ViewModel() {

    private val stepCounts: StateFlow<Int> =
        appDataStore.dailyTotalStepsFlow.filterNotNull()
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _uiState = MutableStateFlow(StepCounterUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            stepCounts.collectLatest { stepCount ->
                _uiState.update {
                    it.copy(stepCounts = stepCount)
                }
            }
        }
    }

    data class StepCounterUiState(
        val stepCounts: Int = 0,
        val goalSteps: Int = 1000
    ) {
        val remainingSteps: Int get() = 1000 - stepCounts
        val progress get() = stepCounts.toFloat() / goalSteps
    }
}