package org.company.app.tracker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.company.app.screenshot.Screenshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random


class HomeViewModelForTimer : ViewModel() {
    private val _workingTime = MutableStateFlow(0)
    val workingTime: StateFlow<Int> = _workingTime

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private val _idleTime = MutableStateFlow(0)
    val idleTime: StateFlow<Int> = _idleTime

    var isTimerStarted = MutableStateFlow(false)

    private var timerJob: Job? = null

    private val screenShot = MutableStateFlow<Screenshot?>(null)
    val screenShotState: StateFlow<Screenshot?> = screenShot

    private var viewModelJob: Job? = null
    private val screenShotScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isPaused = false


    var seconds by mutableStateOf(0)
    var isRunning by mutableStateOf(false)

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun takeScreenShot() {
        screenShot.value = screenShotState.value?.copy(
            screenshot = screenShot.value?.screenshot,
            screenshotTakenTime = screenShot.value?.screenshotTakenTime ?: 0
        )
    }

    fun startTask() {
        if (viewModelJob == null || viewModelJob?.isCancelled == true) {
            viewModelJob = viewModelScope.launch {
                while (isActive) {
                    if (!isPaused) {
                        val randomDelay =
                            Random.nextLong(0, 10 * 60 * 1000) // Random delay within 10 minutes
                        delay(randomDelay)

                        takeScreenShot()

                        delay(10 * 60 * 1000 - randomDelay)
                    } else {
                        delay(1000)
                    }
                }
            }
        }
    }

    fun pauseTask() {
        isPaused = true
    }

    fun resumeTask() {
        isPaused = false
    }

    fun stopTask() {
        viewModelJob?.cancel()
        viewModelJob = null
    }

    init {
        startTask()
    }

}
