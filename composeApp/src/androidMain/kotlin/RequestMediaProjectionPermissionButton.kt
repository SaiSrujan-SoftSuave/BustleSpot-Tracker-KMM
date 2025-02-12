// File: androidApp/src/main/kotlin/org/company/app/ui/RequestMediaProjectionPermissionButton.kt
package org.company.app.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.company.app.MediaProjectionHolder
@Composable
fun RequestMediaProjectionPermissionButton() {
    val context = LocalContext.current
    val activity = context as? Activity
    val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    // Start the foreground service for MediaProjection.
    activity?.let {
        val serviceIntent = Intent(it, MediaProjectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.startForegroundService(serviceIntent)
        } else {
            it.startService(serviceIntent)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                MediaProjectionHolder.mediaProjection =
                    mediaProjectionManager.getMediaProjection(result.resultCode, data)
            }
        }
    }

    Button(onClick = {
        val intent = mediaProjectionManager.createScreenCaptureIntent()
        launcher.launch(intent)
    }) {
        Text("Request Screen Capture Permission")
    }
}
