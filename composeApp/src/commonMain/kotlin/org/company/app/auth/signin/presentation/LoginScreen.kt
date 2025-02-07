package org.company.app.auth.signin.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import compose_multiplatform_app.composeapp.generated.resources.Res
import compose_multiplatform_app.composeapp.generated.resources.ic_bustlespot
import compose_multiplatform_app.composeapp.generated.resources.ic_password_visible
import kotlinx.coroutines.launch
import org.company.app.MainViewModel
import org.company.app.SessionManager
import org.company.app.auth.navigation.AuthScreen
import org.company.app.auth.navigation.Home
import org.company.app.auth.signin.data.SignInResponse
import org.company.app.auth.utils.LoadingScreen
import org.company.app.auth.utils.UiEvent
import org.company.app.mainnavigation.Graph
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
fun LoginScreen(
    navController: NavHostController
) {
    val loginViewModel = koinViewModel<LoginViewModel>()
    val mainViewModel = koinViewModel<MainViewModel>()
    val emailState = loginViewModel.email.collectAsState()
    val passwordState = loginViewModel.password.collectAsState()
    val uiEvent by loginViewModel.uiEvent.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sessionManager : SessionManager = koinInject()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Red,
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp).verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(Res.drawable.ic_bustlespot),
                contentDescription = "Bustlespot Logo",
                modifier = Modifier.size(150.dp)
            )


            Box(modifier = Modifier.background(Color.White, shape = RoundedCornerShape(20.dp))) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Sign In",
                        color = Color.Black,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Box(
                        modifier = Modifier.width(64.dp).height(5.dp).background(
                            Color.Red,
                            RoundedCornerShape(20.dp)
                        )
                    )

                    TextField(
                        value = emailState.value.value,
                        onValueChange = { loginViewModel.onEvent(LoginEvent.EmailChanged(it)) },
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                            errorIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                            focusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        ),
                        placeholder = { Text("Email", modifier = Modifier.alpha(0.5f)) },
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        isError = emailState.value.error.isNotEmpty(),
                        maxLines = 1,
                        trailingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        supportingText = {
                            if (emailState.value.error.isNotEmpty()) {
                                Text(
                                    text = emailState.value.error,
                                    fontFamily = androidx.compose.material3.MaterialTheme.typography.bodyMedium.fontFamily,
                                    fontWeight = FontWeight.Light,
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    TextField(
                        value = passwordState.value.value,
                        onValueChange = { loginViewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                            errorIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                            focusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        ),
                        placeholder = { Text("Password", modifier = Modifier.alpha(0.5f)) },
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        maxLines = 1,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_password_visible),
                                contentDescription = "Email Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        isError = passwordState.value.error.isNotEmpty(),
                        supportingText = {
                            if (passwordState.value.error.isNotEmpty()) {
                                Text(
                                    text = passwordState.value.error,
                                    fontFamily = androidx.compose.material3.MaterialTheme.typography.bodyMedium.fontFamily,
                                    fontWeight = FontWeight.Light,
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp).clickable {
                                navController.navigate(AuthScreen.ForgotPassword.route)
                            },
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Button(
                        onClick = {
                            loginViewModel.onEvent(LoginEvent.SubmitLogin)
                        },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        enabled = emailState.value.isValid && passwordState.value.isValid
                    ) {
                        Text(
                            text = "Login",
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Don't have an account?",
                            color = Color.Black,
                            modifier = Modifier.padding(end = 8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = "Sign Up",
                            color = Color.Red,
                            modifier = Modifier.clickable {
                                navController.navigate(AuthScreen.SignUp.route)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Let's Start!",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "version",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        when (uiEvent) {
            is UiEvent.Failure -> {
                rememberCoroutineScope().launch {
                    snackbarHostState.showSnackbar((uiEvent as UiEvent.Failure).error)
                }
            }

            UiEvent.Loading -> {
                LoadingScreen()
            }

            is UiEvent.Success -> {
                rememberCoroutineScope().launch {
                    snackbarHostState.showSnackbar("Completed Login Successfully")
                    (uiEvent as UiEvent.Success<SignInResponse>).data.access_token?.let { newToken ->
                        sessionManager.updateAccessToken(
                            newToken
                        )
                    } ?: println("Token not found")
                        mainViewModel.fetchAccessToken()
                        navController.navigate(Home.Organisation.route) {
                            popUpTo(Graph.AUTHENTICATION) {
                                inclusive = true
                            }

                    }
                }
            }

            null -> {
                println("null")
            }
        }
    }
}
