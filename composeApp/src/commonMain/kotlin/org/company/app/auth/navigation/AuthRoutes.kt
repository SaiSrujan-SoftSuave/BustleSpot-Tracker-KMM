package org.company.app.auth.navigation


sealed class AuthScreen(val route: String) {
    data object SignIn : AuthScreen("login")
    data object SignUp : AuthScreen("signup")
    data object ForgotPassword : AuthScreen("forgot_password")
    data object ChangePassword : AuthScreen("change_password")
    data object LogOut : AuthScreen("logout")
}

sealed class Home(val route: String) {
    data object Organisation : Home("organisation")
    data object Tracker : Home("tracker")
}



