package org.company.app.screenshot

import androidx.compose.ui.graphics.ImageBitmap

data class Screenshot(
    val screenshot: ImageBitmap?,
    val screenshotTakenTime: Long
)
