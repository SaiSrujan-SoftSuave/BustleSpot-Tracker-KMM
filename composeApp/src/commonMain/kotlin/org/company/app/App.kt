package org.company.app


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import org.company.app.mainnavigation.RootNavigationGraph


@Composable
internal fun App() {
    val navController = rememberNavController()
    MaterialTheme {
        RootNavigationGraph(navController)
    }
}

