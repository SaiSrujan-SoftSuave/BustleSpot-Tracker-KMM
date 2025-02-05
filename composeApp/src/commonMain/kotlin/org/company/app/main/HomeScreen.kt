package org.company.app.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.company.app.auth.utils.secondsToTime
import org.company.app.timer.TrackerViewModel
import org.company.app.tracker.ui.HomeViewModelForTimer

import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun TrackerScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val homeViewModelForTimer = koinViewModel<HomeViewModelForTimer>()
    val trackerViewModel = koinViewModel<TrackerViewModel>()

    val workingTime by trackerViewModel.trackerTime.collectAsState()
    val isRunning by trackerViewModel.isTrackerRunning.collectAsState()



    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            val timer by remember { mutableStateOf(0) }
            var buttonState by remember { mutableStateOf(false) }
            Text(text = "Tracker Screen", style = MaterialTheme.typography.titleLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        buttonState = !buttonState
                        if (buttonState) {
                            trackerViewModel.startTimer()
                        } else {
                            trackerViewModel.stopTimer()
                        }
                    }) {
                        if (buttonState) {
                            Text(text = "Pause")
                        } else if (isRunning) {
                            Text(text = "resume")
                        } else {
                            Text(text = "Start")
                        }
                    }
                    Button(onClick = {
                        trackerViewModel.resetTimer()
                    }) {
                        Text(text = "Reset")
                    }
                }
            }
            Text(text = "Timer: ${secondsToTime(workingTime)}")


            Button(onClick = {
                homeViewModelForTimer.takeScreenShot()
            }) {
                Text(text = "Take Screenshot")
            }



            homeViewModelForTimer.screenShotState.collectAsState().value?.let {
                it.screenshot?.let { it1 ->
                    Image(
                        it1,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp)
                    )
                } ?: Text("No screenshot available")
            } ?: Text("No screenshot available")
        }
    }
}