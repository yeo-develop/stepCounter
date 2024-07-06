package com.yeo.develop.stepcounter.activities.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yeo.develop.stepcounter.activities.viewmodel.StepCounterViewModel
import com.yeo.develop.stepcounter.ui.AccentBlue
import com.yeo.develop.stepcounter.ui.BackgroundBlue
import com.yeo.develop.stepcounter.ui.LightBlue
import com.yeo.develop.stepcounter.ui.PrimaryBlue

@Composable
fun StepCounterScreen(
    viewModel: StepCounterViewModel = hiltViewModel()
) {
    val currentSteps by viewModel.stepCounts.collectAsState()
    val goalSteps by remember { mutableStateOf(1000) }

    val remainingSteps = goalSteps - currentSteps
    val progress = currentSteps.toFloat() / goalSteps

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(BackgroundBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .padding(top = 40.dp)
            ) {
                Text(text = "오늘 걸음 수", fontSize = 24.sp)
                Text(
                    text = currentSteps.toString(),
                    fontSize = 72.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Slider(
                value = progress,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
                    .padding(horizontal = 40.dp),
                enabled = true,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryBlue,  // Slider thumb color
                    activeTrackColor = AccentBlue,  // Active track color
                    inactiveTrackColor = LightBlue  // Inactive track color
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(PrimaryBlue)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .padding(horizontal = 40.dp)
            ) {
                Text(text = "나의 목표 걸음 수", fontSize = 22.sp, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = goalSteps.toString(),
                        color = Color.White,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " 걸음",
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp, bottom = 30.dp)
                        .background(color = Color.White, RoundedCornerShape(10.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "목표 달성까지\n$remainingSteps 걸음 남았어요!",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
fun StepCounterScreenPreview() {
    StepCounterScreenPreview()
}
