package org.company.app.mainnavigation

import org.company.app.SessionManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.company.app.auth.navigation.authNavGraph
import org.company.app.auth.navigation.homeNavGraph
import org.koin.compose.koinInject


@Composable
fun RootNavigationGraph(navController: NavHostController,onFocusReceived: () -> Unit = {}) {
    val sessionManager: SessionManager = koinInject()
    // Observe the mutable state directly
    val isLoggedIn = sessionManager.isLoggedIn


    LaunchedEffect(sessionManager.isLoggedIn){
        if (!sessionManager.isLoggedIn){
            navController.navigate(
                route = Graph.AUTHENTICATION
            )
        }
    }

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = if (isLoggedIn) Graph.HOME else Graph.AUTHENTICATION
    ) {
        authNavGraph(navController)
        homeNavGraph(navController,onFocusReceived)
    }
}



object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val ORGANISATION = "organisation_graph"
}
