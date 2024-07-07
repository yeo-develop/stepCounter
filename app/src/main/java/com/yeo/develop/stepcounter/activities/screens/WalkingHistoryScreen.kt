package com.yeo.develop.stepcounter.activities.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yeo.develop.stepcounter.activities.viewmodel.WalkingHistoryViewModel
import com.yeo.develop.stepcounter.database.steps.StepDataEntity
import com.yeo.develop.stepcounter.ui.PrimaryBlue


@Composable
fun WalkingHistoryScreen(viewModel: WalkingHistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 40.dp)
        ) {
            Text(
                text = "총 ${uiState.totalStepCount}보를 걸었어요!",
                fontSize = 24.sp,
            )
            Text(
                text = "이를 칼로리로 환산하면 ${String.format("%.1f", uiState.totalCaloriesBurned)} 칼로리에요",
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "상세 내역은 아래에서 확인할 수 있어요.",
                fontSize = 16.sp,
            )
        }
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "상세 내역",
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                userScrollEnabled = true
            ) {
                items(uiState.dailyStepHistory) { stepData ->
                    StepHistoryItem(stepData)
                }
            }
        }
    }
}

@Composable
fun StepHistoryItem(stepData: StepDataEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(
                width = 2.dp,
                color = PrimaryBlue,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Column(Modifier.padding(horizontal = 40.dp, vertical = 10.dp)) {
            Text(text = stepData.targetDate)
            Text(text = "${stepData.steps}걸음")
        }
    }
}