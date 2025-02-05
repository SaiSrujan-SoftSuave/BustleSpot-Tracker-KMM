package org.company.app.auth.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties


@Composable
fun CustomAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {},
        title = {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = text,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    )
}