// File: androidApp/src/main/kotlin/org/company/app/screenshot/ScreenshotProvider.actual.kt
package org.company.app.screenshot

import android.media.projection.MediaProjection
import androidx.compose.ui.graphics.ImageBitmap
import com.company.app.screenshot.ScreenshotProvider
import org.koin.java.KoinJavaComponent

actual suspend fun takeScreenShot(): ImageBitmap? {
    // Retrieve MediaProjection and Context via Koin.
    // Use getOrNull to safely check if MediaProjection is available.
    val mediaProjection: MediaProjection? = KoinJavaComponent.getOrNull(MediaProjection::class.java)
    if (mediaProjection == null) {
        throw IllegalStateException("MediaProjection is not available. Request permission again.")
    }
    val context = KoinJavaComponent.get<android.content.Context>(android.content.Context::class.java)

    val screenshotProvider = ScreenshotProvider(context, mediaProjection)
    // takeSystemScreenshot() will stop the projection at the end.
    return screenshotProvider.takeSystemScreenshot()
}
