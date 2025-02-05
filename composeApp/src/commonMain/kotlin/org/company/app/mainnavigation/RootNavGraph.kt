package org.company.app.mainnavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.company.app.MainViewModel
import org.company.app.auth.navigation.authNavGraph
import org.company.app.auth.navigation.homeNavGraph
import org.company.app.auth.utils.LoadingScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun RootNavigationGraph(navController: NavHostController) {
    val mainViewModel = koinViewModel<MainViewModel>()

    LaunchedEffect(Unit) {
        if (mainViewModel.isLoading.value) {
            mainViewModel.fetchAccessToken()
        }
    }

    val isLoading = mainViewModel.isLoading.collectAsState().value
    val isLoggedIn = mainViewModel.isLoggedIn

    if (isLoading) {
        LoadingScreen()
    } else {
        NavHost(
            navController = navController,
            route = Graph.ROOT,
            startDestination = if (isLoggedIn) Graph.HOME else Graph.AUTHENTICATION
        ) {
//            println(mainViewModel.accessToken.value)
            authNavGraph(navController)
            homeNavGraph(navController)
        }
    }
}


object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val ORGANISATION = "organisation_graph"
}
