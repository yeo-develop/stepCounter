package com.yeo.develop.stepcounter.activities.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeo.develop.stepcounter.ApplicationConstants
import com.yeo.develop.stepcounter.datastore.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StepCounterViewModel @Inject constructor(
    private val appDataStore: AppDataStore
) : ViewModel() {

    private val dateTimeFormatter =
        DateTimeFormatter.ofPattern(ApplicationConstants.ENTITY_DATE_FORMAT)

    val stepCounts: StateFlow<Int> =
        appDataStore.dailyTotalStepsFlow.filterNotNull()
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
}