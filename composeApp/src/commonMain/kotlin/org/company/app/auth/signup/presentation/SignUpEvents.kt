package org.company.app.auth.signup.presentation


// Sealed class for login events
sealed class SignUpEvents {
    data class FirstNameChanged(val firstName: String) : SignUpEvents()
    data class LastNameChanged(val lastName: String) : SignUpEvents()
    data class EmailChanged(val email: String) : SignUpEvents()
    data class PasswordChanged(val password: String) : SignUpEvents()
    object SubmitSignUp : SignUpEvents()
}