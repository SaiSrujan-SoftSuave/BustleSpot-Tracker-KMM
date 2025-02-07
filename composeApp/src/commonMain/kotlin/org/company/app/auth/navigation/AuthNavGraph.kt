package org.company.app.auth.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.company.app.auth.changepassword.presentation.ChangePasswordScreen
import org.company.app.auth.forgotpassword.presentation.ForgotPasswordScreen
import org.company.app.auth.logout.presentation.LogoutScreen
import org.company.app.auth.signin.presentation.LoginScreen
import org.company.app.auth.signup.presentation.SingUpScreen
import org.company.app.mainnavigation.Graph
import org.company.app.organisation.ui.OrganisationScreen
import org.company.app.tracker.ui.TrackerScreen


fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.SignIn.route
    ) {
        composable(AuthScreen.SignIn.route) {
            LoginScreen(navController = navController)
        }
        composable(AuthScreen.SignUp.route) {
            SingUpScreen(navController = navController)
        }
        composable(AuthScreen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(AuthScreen.ChangePassword.route) {
            ChangePasswordScreen(navController = navController)
        }
        composable(AuthScreen.LogOut.route) {
            LogoutScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    onFocusReceived: () -> Unit = {}
) {
    navigation(
        route = Graph.HOME,
        startDestination = Home.Organisation.route
    ) {
        composable(Home.Organisation.route) {
            OrganisationScreen(navController = navController)
        }
        composable(
            route = "${Home.Tracker.route}/{orgId}",
            arguments = listOf(
                navArgument("orgId") {
                    defaultValue = "0"
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val orgId = navBackStackEntry.arguments?.getString("orgId")
            orgId?.let { it ->
                TrackerScreen(navController, organisationName = it, onFocusReceived = onFocusReceived)
            }

        }
    }
}