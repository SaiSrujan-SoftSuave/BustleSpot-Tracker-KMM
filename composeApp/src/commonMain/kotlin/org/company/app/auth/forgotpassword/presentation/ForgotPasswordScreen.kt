package org.company.app.auth.forgotpassword.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier.fillMaxSize().clickable {
            navController.popBackStack()
        },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Forgot Password",
        )
    }
}