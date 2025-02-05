package org.company.app.auth.utils

fun validateEmail(email: String): String? {
    if (email.isEmpty()) {
        return "Email cannot be empty"
    }
    if (!isValidEmail(email)) {
        return "Invalid email format"
    }
    return null
}

fun validatePassword(password: String): String? {
    if (password.isEmpty()) {
        return "Password cannot be empty"
    }
    if (password.length < 8) {
        return "Password must be at least 8 characters long"
    }
    if (!password.any { it.isUpperCase() }) {
        return "Password must contain at least one uppercase letter"
    }
    if (!password.any { it.isLowerCase() }) {
        return "Password must contain at least one lowercase letter"
    }
    if (!password.any { it.isDigit() }) {
        return "Password must contain at least one digit"
    }
    if (!password.any { "!@#$%^&*()-_=+[]{}|;:',.<>?/`~".contains(it) }) {
        return "Password must contain at least one special character (e.g., @, $, %, &, etc.)"
    }
    if (password.contains(" ")) {
        return "Password must not contain spaces"
    }
    return null
}

fun validateInput(email: String, password: String): Unit? {
    if (validateEmail(email) != null && validatePassword(password) != null) {
        return null
    }
    return Unit
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    return email.matches(emailRegex)
}

fun validateFirstName(firstName: String): String? {
    if (firstName.isEmpty()) {
        return "First name cannot be empty"
    }
    return null
}

fun validateLastName(lastName: String): String? {
    if (lastName.isEmpty()) {
        return "Last name cannot be empty"
    }
    return null
}
