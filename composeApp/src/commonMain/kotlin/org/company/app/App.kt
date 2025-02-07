package org.company.app


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import org.company.app.mainnavigation.RootNavigationGraph


@Composable
internal fun App(onFocusReceived: () -> Unit = {}) {
    val navController = rememberNavController()
    MaterialTheme {
        RootNavigationGraph(navController, onFocusReceived)
    }
}

