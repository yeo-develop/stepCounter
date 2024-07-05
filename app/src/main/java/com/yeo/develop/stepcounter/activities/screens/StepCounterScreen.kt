package com.yeo.develop.stepcounter.activities.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.yeo.develop.stepcounter.activities.viewmodel.MainViewModel

@Composable
fun StepCounterScreen(
    viewModel: MainViewModel = hiltViewModel(),
    startButtonClicked: () -> Unit,
    stopButtonClicked: () -> Unit
) {
    val currentStep by viewModel.stepCounts.collectAsState(initial = 0)
    val today by viewModel.today.collectAsState(initial = "")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "날짜: $today")
        Text(text = "현재 걸음: ${currentStep ?: 0} 걸음") // null 걸음은 조금 이상하죠..
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                startButtonClicked()
            }) {
                Text(text = "적산 시작")
            }

            Button(onClick = {
                stopButtonClicked()
            }) {
                Text(text = "적산 종료")
            }
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
fun StepCounterScreenPreview() {
    StepCounterScreenPreview()
}
