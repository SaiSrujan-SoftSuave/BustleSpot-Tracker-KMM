package org.company.app.auth.signup.data

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val message: String
)