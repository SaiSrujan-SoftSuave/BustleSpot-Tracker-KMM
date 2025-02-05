package org.company.app.auth.utils

fun secondsToTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    val hoursStr = if (hours < 10) "0$hours" else "$hours"
    val minutesStr = if (minutes < 10) "0$minutes" else "$minutes"
    val secsStr = if (secs < 10) "0$secs" else "$secs"

    return "$hoursStr:$minutesStr:$secsStr"
}