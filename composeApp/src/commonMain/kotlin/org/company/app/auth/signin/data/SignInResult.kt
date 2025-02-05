package org.company.app.auth.signin.data

import kotlinx.serialization.Serializable


@Serializable
data class SignInResponse(
    val status : Int?,
    val access_token : String?
)
