package org.company.app.auth.signup.data

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String
)