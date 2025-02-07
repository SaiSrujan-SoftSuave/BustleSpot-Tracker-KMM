package org.company.app.timer

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softsuave.bustlespotsample.GlobalEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random


actual class TrackerViewModel : ViewModel() {
    actual var trackerTime: MutableStateFlow<Int> = MutableStateFlow(0)
        get() = field
    actual var isTrackerRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
        get() = field
    actual var isIdealTimerRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
        get() = field
    actual var idealTime: MutableStateFlow<Int> = MutableStateFlow(0)
        get() = field

    actual var screenShotTakenTime: MutableStateFlow<Int> = MutableStateFlow(0)
        get() = field
    actual var keyboradKeyEvents: MutableStateFlow<Int> = MutableStateFlow(0)
        get() = field
    actual var mouseKeyEvents: MutableStateFlow<Int> = MutableStateFlow(0)
        get() = field
    actual var mouseMotionCount: MutableStateFlow<Int> = MutableStateFlow(0)
        get() = field
    actual var customeTimeForIdleTime: MutableStateFlow<Int> = MutableStateFlow(480)
        get() = field
    actual var numberOfScreenshot: MutableStateFlow<Int> = MutableStateFlow(1)
        get() = field

    private var timer = Timer()
    private var isTaskScheduled = AtomicBoolean(false)
    private var isIdleTaskScheduled = AtomicBoolean(false)

    private val globalEventListener: GlobalEventListener = GlobalEventListener()
    private val screenShot = MutableStateFlow<ImageBitmap?>(null)
    actual val screenShotState: StateFlow<ImageBitmap?> = screenShot

    private val randomTime: MutableStateFlow<Int> = MutableStateFlow(0)
    private var viewModelJob: Job? = null
    private val screenShotScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isPaused = false


    actual fun resetTimer() {
        isTrackerRunning.value = false
        idealTime.value = 0
        trackerTime.value = 0
    }

    actual fun stopTimer() {
        isTrackerRunning.value = false
        globalEventListener.unregisterListeners()
    }

    actual fun resumeTracker() {
        isTrackerRunning.value = true
        globalEventListener.registerListeners()
    }
//    actual fun registerGlobalEventListener(){
//        globalEventListener.registerEventTracking()
//    }
//    actual fun unregisterGlobalEventListener(){
//        globalEventListener.unregisterEventTracking()
//    }


    private fun setRandomTime() {
        randomTime.value = Random.nextInt(from = 1, until = 10)
    }

    actual fun startTimer() {
        isTrackerRunning.value = true
        setRandomTime()
        globalEventListener.registerListeners()
        viewModelScope.launch {
            globalEventListener.fKeyCount.collectLatest { it ->
                keyboradKeyEvents.emit(it)
                idealTime.value = 0
            }
        }
        viewModelScope.launch {
            globalEventListener.fMouseCount.collectLatest { it ->
                mouseKeyEvents.emit(it)
                idealTime.value = 0
            }
        }
        viewModelScope.launch {
            globalEventListener.fMouseMotionCount.collectLatest { it ->
                mouseMotionCount.emit(it)
                idealTime.value = 0
            }
        }
        if (!isIdleTaskScheduled.getAndSet(true)) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    idealTime.value += 1
                }
            }, 1000, 1000)
        }


        if (!isTaskScheduled.getAndSet(true)) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (isTrackerRunning.value) {
                        trackerTime.value += 1
                        screenShotTakenTime.value += (trackerTime.value % 3600) / 60
                        val min = (trackerTime.value % 3600) / 60
                        println(min)
                        if (min % 10 == randomTime.value) {
                            takeScreenShot()
                        }
                    }
                }
            }, 1000, 1000)
        }
    }


    //idle timer
    actual fun resetIdleTimer() {
        isIdealTimerRunning.value = false
        idealTime.value = 0
    }

    actual fun stopIdleTimer() {
        isIdealTimerRunning.value = false
    }

    actual fun startIdleTimerClock() {
        isIdealTimerRunning.value = true
    }

    fun takeScreenShot() {
        screenShot.value = org.company.app.screenshot.takeScreenShot()
        screenShotTakenTime.value = 0
    }


    // screenshots

    actual fun startScreenshotTask() {
        if (viewModelJob == null || viewModelJob?.isCancelled == true) {
            viewModelJob = viewModelScope.launch {
                while (isActive) {
                    if (!isPaused) {
                        val randomDelay =
                            Random.nextLong(0, 60 * 1000) // Random delay within 10 minutes
                        delay(randomDelay)

                        takeScreenShot()

                        delay(60 * 1000 - randomDelay)
                    } else {
                        delay(1000)
                    }
                }
            }
        }
    }

    actual fun pauseScreenshotTask() {
        isPaused = true
    }

    actual fun resumeScreenshotTask() {
        isPaused = false
    }

    actual fun stopScreenshotTask() {
        viewModelJob?.cancel()
        viewModelJob = null
    }

    actual fun updateTrackerTimer() {
        trackerTime.value -= customeTimeForIdleTime.value
    }

    actual fun startIdleTimer() {
    }

    actual fun addCustomTimeForIdleTime(time: Int) {
        customeTimeForIdleTime.value = time
    }

}