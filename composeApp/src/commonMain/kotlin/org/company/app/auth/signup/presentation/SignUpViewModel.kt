package org.company.app.auth.signup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.company.app.auth.signup.data.SignUpRepository
import org.company.app.auth.signup.data.SignUpResponse
import org.company.app.auth.utils.CustomTextFieldState
import org.company.app.auth.utils.Result
import org.company.app.auth.utils.UiEvent
import org.company.app.auth.utils.validateEmail
import org.company.app.auth.utils.validateFirstName
import org.company.app.auth.utils.validatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: SignUpRepository
) : ViewModel() {


    private val _firstName: MutableStateFlow<CustomTextFieldState> =
        MutableStateFlow(CustomTextFieldState())
    val firstName: StateFlow<CustomTextFieldState> = _firstName

    private val _lastName: MutableStateFlow<CustomTextFieldState> =
        MutableStateFlow(CustomTextFieldState())
    val lastName: StateFlow<CustomTextFieldState> = _lastName

    private val _email: MutableStateFlow<CustomTextFieldState> =
        MutableStateFlow(CustomTextFieldState())
    val email: StateFlow<CustomTextFieldState> = _email

    private val _password: MutableStateFlow<CustomTextFieldState> =
        MutableStateFlow(CustomTextFieldState())
    val password: StateFlow<CustomTextFieldState> = _password


    private val _uiEvent: MutableStateFlow<UiEvent<SignUpResponse>?> = MutableStateFlow(null)
    val uiEvent: StateFlow<UiEvent<SignUpResponse>?> = _uiEvent


    fun onEvent(event: SignUpEvents) {
        when (event) {
            is SignUpEvents.EmailChanged -> {
                val validationResult = validateEmail(event.email)
                _email.value = _email.value.copy(
                    value = event.email,
                    error = validationResult ?: "",
                    isValid = validationResult == null
                )
            }

            is SignUpEvents.PasswordChanged -> {
                val validationResult = validatePassword(event.password)
                _password.value = _password.value.copy(
                    value = event.password,
                    error = validationResult ?: "",
                    isValid = validationResult == null
                )
            }

            is SignUpEvents.SubmitSignUp -> {
                viewModelScope.launch {

                    repository.signUp(
                        firstName.value.value,
                        lastName.value.value,
                        email.value.value,
                        password.value.value
                    ).collect {
                        when (it) {
                            is Result.Error -> {
                                println("Error: ${it.message}")
                            }

                            Result.Loading -> {

                            }

                            is Result.Success -> {

                            }
                        }
                    }
                }
            }

            is SignUpEvents.FirstNameChanged -> {
                val validationResult = validateFirstName(event.firstName)
                _firstName.value = _firstName.value.copy(
                    value = event.firstName,
                    error = validationResult ?: "",
                    isValid = validationResult == null
                )
            }

            is SignUpEvents.LastNameChanged -> {
                val validationResult = validateFirstName(event.lastName)
                _lastName.value = _lastName.value.copy(
                    value = event.lastName,
                    error = validationResult ?: "",
                    isValid = validationResult == null
                )
            }
        }
    }

}