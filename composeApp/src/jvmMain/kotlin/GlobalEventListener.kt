import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent
import com.github.kwhat.jnativehook.mouse.NativeMouseListener
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener
import kotlinx.coroutines.flow.MutableStateFlow

class GlobalEventListener : NativeKeyListener, NativeMouseListener, NativeMouseMotionListener {
    private var keyCount = 0
    private var mouseCount = 0
    private var mouseMotionCount = 0

    val fKeyCount = MutableStateFlow(0)
    val fMouseCount = MutableStateFlow(0)
    val fMouseMotionCount = MutableStateFlow(0)

    private fun registerEventTracking() {
        try {
            GlobalScreen.registerNativeHook()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    fun registerListeners() {
        if (!GlobalScreen.isNativeHookRegistered()) {
            registerEventTracking()
        }
        GlobalScreen.addNativeKeyListener(this)
        GlobalScreen.addNativeMouseListener(this)
        GlobalScreen.addNativeMouseMotionListener(this)
    }

    fun unregisterListeners() {
        GlobalScreen.removeNativeKeyListener(this)
        GlobalScreen.removeNativeMouseListener(this)
        GlobalScreen.removeNativeMouseMotionListener(this)
    }

    fun unregisterEventTracking() {
        try {
            GlobalScreen.unregisterNativeHook()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getKeyCount(): Int = keyCount
    fun getMouseCount(): Int = mouseCount
    fun resetCount() {
        keyCount = 0
        mouseCount = 0
        mouseMotionCount = 0
        fKeyCount.value = 0
        fMouseCount.value = 0
        fMouseMotionCount.value = 0
    }

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        keyCount++
        fKeyCount.value = keyCount
        println("Key pressed: ${NativeKeyEvent.getKeyText(e.keyCode)} | Total: $keyCount")
    }

    override fun nativeMouseClicked(e: NativeMouseEvent) {
        mouseCount++
        fMouseCount.value = mouseCount
        println("Mouse clicked at (${e.x}, ${e.y}) | Total: ${fMouseCount.value}")
    }

    override fun nativeMouseMoved(e: NativeMouseEvent) {
        mouseMotionCount++
        fMouseMotionCount.value = mouseMotionCount
        println("Mouse moved to (${e.x}, ${e.y}) | Total: ${fMouseMotionCount.value}")
        // Optional: Handle mouse movement if needed
    }

    // Implement other required methods with empty bodies if not used
    override fun nativeKeyReleased(e: NativeKeyEvent?) {}
    override fun nativeKeyTyped(e: NativeKeyEvent?) {}
    override fun nativeMousePressed(e: NativeMouseEvent?) {}
    override fun nativeMouseReleased(e: NativeMouseEvent?) {}
    override fun nativeMouseDragged(e: NativeMouseEvent?) {}
}