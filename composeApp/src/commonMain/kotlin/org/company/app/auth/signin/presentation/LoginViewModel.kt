package org.company.app.auth.signin.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.company.app.SessionManager
import org.company.app.auth.signin.data.SignInRepository
import org.company.app.auth.signin.data.SignInResponse
import org.company.app.auth.utils.CustomTextFieldState
import org.company.app.auth.utils.Result
import org.company.app.auth.utils.UiEvent
import org.company.app.auth.utils.validateEmail
import org.company.app.auth.utils.validatePassword

class LoginViewModel(
    private val repository: SignInRepository,
    private val sessionManager: SessionManager,
    private val settings: ObservableSettings
) : ViewModel() {

    private val _email: MutableStateFlow<CustomTextFieldState> =
        MutableStateFlow(CustomTextFieldState())
    val email: StateFlow<CustomTextFieldState> = _email

    private val _password: MutableStateFlow<CustomTextFieldState> =
        MutableStateFlow(CustomTextFieldState())
    val password: StateFlow<CustomTextFieldState> = _password

    private val _uiEvent: MutableStateFlow<UiEvent<SignInResponse>?> = MutableStateFlow(null)
    val uiEvent: StateFlow<UiEvent<SignInResponse>?> = _uiEvent


    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                val validationResult = validateEmail(event.email)
                _email.value = _email.value.copy(
                    value = event.email,
                    error = validationResult ?: "",
                    isValid = validationResult == null
                )
            }

            is LoginEvent.PasswordChanged -> {
                val validationResult = validatePassword(event.password)
                _password.value = _password.value.copy(
                    value = event.password,
                    error = validationResult ?: "",
                    isValid = validationResult == null
                )
            }

            is LoginEvent.SubmitLogin -> {
                viewModelScope.launch {
                    repository.signIn(email.value.value, password.value.value).collect { result ->
                        when (result) {
                            is Result.Success -> {
                                launch {
                                    result.data.access_token?.let { newToken ->
                                        sessionManager.updateAccessToken(
                                            newToken
                                        )
                                    } ?: println("Token not found")
                                }
                                _uiEvent.value = UiEvent.Success(result.data)
                            }

                            is Result.Error -> {
                                _uiEvent.value = UiEvent.Failure(result.message ?: "Unknown Error")
                            }

                            Result.Loading -> {
                                _uiEvent.value = UiEvent.Loading
                            }
                        }
                    }
                }
            }

            LoginEvent.TextHttpClint -> viewModelScope.launch {
                repository.testRoot().collectLatest {
                    println("Result: $it")
                }
            }
        }
    }
}


// Sealed class for login events
sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object SubmitLogin : LoginEvent()
    object TextHttpClint : LoginEvent()
}

