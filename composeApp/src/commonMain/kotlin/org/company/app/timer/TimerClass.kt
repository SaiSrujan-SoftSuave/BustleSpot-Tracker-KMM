package org.company.app.timer

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


expect class TrackerViewModel() : ViewModel {
    var trackerTime: MutableStateFlow<Int>
    var idealTime: MutableStateFlow<Int>
    var isTrackerRunning: MutableStateFlow<Boolean>
    var isIdealTimerRunning: MutableStateFlow<Boolean>
    val screenShotState: StateFlow<ImageBitmap?>

    fun startTimer()
    fun resetTimer()
    fun stopTimer()
    fun resumeTracker()
    fun updateTrackerTimer()


    fun resetIdleTimer()
    fun stopIdleTimer()
    fun startIdleTimerClock()
    fun startIdleTimer()

    fun stopScreenshotTask()
    fun startScreenshotTask()
    fun pauseScreenshotTask()
    fun resumeScreenshotTask()
    var screenShotTakenTime: MutableStateFlow<Int>

    var mouseKeyEvents: MutableStateFlow<Int>
    var keyboradKeyEvents: MutableStateFlow<Int>
    var mouseMotionCount: MutableStateFlow<Int>
}