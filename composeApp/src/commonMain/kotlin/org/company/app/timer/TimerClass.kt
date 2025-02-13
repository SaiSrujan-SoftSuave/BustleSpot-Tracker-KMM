package org.company.app.timer

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


expect class TrackerModule(
    viewModelScope: CoroutineScope
) {
    var trackerTime: MutableStateFlow<Int>
    var idealTime: MutableStateFlow<Int>
    var isTrackerRunning: MutableStateFlow<Boolean>
    var isIdealTimerRunning: MutableStateFlow<Boolean>
    val screenShotState: StateFlow<ImageBitmap?>
    var screenShotTakenTime: MutableStateFlow<Int>

    var mouseKeyEvents: MutableStateFlow<Int>
    var keyboradKeyEvents: MutableStateFlow<Int>
    var mouseMotionCount: MutableStateFlow<Int>
    var customeTimeForIdleTime: MutableStateFlow<Int>

    var numberOfScreenshot: MutableStateFlow<Int>

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

    fun addCustomTimeForIdleTime(time: Int)
}