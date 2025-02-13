package org.company.app.timer

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class TrackerModule actual constructor(
    viewModelScope: CoroutineScope
) {
    actual var trackerTime: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var idealTime: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var isTrackerRunning: MutableStateFlow<Boolean>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var isIdealTimerRunning: MutableStateFlow<Boolean>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual val screenShotState: StateFlow<ImageBitmap?>
        get() = TODO("Not yet implemented")

    actual fun startTimer() {
    }

    actual fun resetTimer() {
    }

    actual fun stopTimer() {
    }

    actual fun resumeTracker() {
    }

    actual fun updateTrackerTimer() {
    }

    actual fun resetIdleTimer() {
    }

    actual fun stopIdleTimer() {
    }

    actual fun startIdleTimerClock() {
    }

    actual fun startIdleTimer() {
    }

    actual fun stopScreenshotTask() {
    }

    actual fun startScreenshotTask() {
    }

    actual fun pauseScreenshotTask() {
    }

    actual fun resumeScreenshotTask() {
    }

    actual var screenShotTakenTime: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var mouseKeyEvents: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var keyboradKeyEvents: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var mouseMotionCount: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var customeTimeForIdleTime: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var numberOfScreenshot: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
        set(value) {}

    actual fun addCustomTimeForIdleTime(time: Int) {
    }


}