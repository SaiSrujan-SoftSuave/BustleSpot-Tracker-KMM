package com.company.app.screenshot

import org.company.app.MediaProjectionHolder


// File: androidApp/src/main/kotlin/org/company/app/screenshot/ScreenshotProvider.kt
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ScreenshotProvider(
    private val context: Context,
    private var mediaProjection: MediaProjection
) {
    suspend fun takeSystemScreenshot(): ImageBitmap? {
        // Get screen dimensions.
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        // Register a callback to manage the projection's state.
        val projectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                // Handle if needed.
            }
        }
        mediaProjection.registerCallback(projectionCallback, Handler(Looper.getMainLooper()))

        // Create an ImageReader.
        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        // Create a VirtualDisplay. (This call will throw if the projection is no longer current.)
        val virtualDisplay: VirtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )

        // Wait for an image.
        val image: Image = suspendCancellableCoroutine { cont ->
            imageReader.setOnImageAvailableListener({ reader ->
                reader.acquireLatestImage()?.let { img ->
                    cont.resume(img)
                }
            }, Handler(Looper.getMainLooper()))
        }

        // Convert the image to a Bitmap.
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width

        val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()

        // Release resources.
        virtualDisplay.release()
        imageReader.close()
        mediaProjection.unregisterCallback(projectionCallback)

        // Stop the projection because we have captured the screenshot.
        mediaProjection.stop()
        MediaProjectionHolder.mediaProjection = null

        // Crop the bitmap to the actual screen dimensions.
        val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
        return croppedBitmap.asImageBitmap()
    }
}
