package com.yeo.develop.stepcounter.activities.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeo.develop.stepcounter.ApplicationConstants
import com.yeo.develop.stepcounter.database.steps.StepDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stepDao: StepDao
) : ViewModel() {

    private val dateTimeFormatter =
        DateTimeFormatter.ofPattern(ApplicationConstants.ENTITY_DATE_FORMAT)

    private val _today = MutableStateFlow(LocalDate.now().format(dateTimeFormatter))
    val today = _today.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val stepCounts: StateFlow<Int?> = today.flatMapLatest {
        stepDao.getStepsByDateFlow(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /**
     * 유저가 앱을 켜놓은상태에서 긴 시간 사용하지 않은상태 (날짜가 지났을때) 이전날의 데이터를 계속 보여주게되는 불상사를 방지합시다.
     * */
    fun checkLocalVariableCurrentTime() {
        val updatedTime = LocalDate.now().format(dateTimeFormatter)
        if (updatedTime != today.value) {
            _today.value = updatedTime.format(dateTimeFormatter)
        }
    }
}