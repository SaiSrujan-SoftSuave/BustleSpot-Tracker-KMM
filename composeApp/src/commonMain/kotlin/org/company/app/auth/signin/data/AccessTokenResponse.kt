package org.company.app.auth.signin.data

import kotlinx.serialization.Serializable


@Serializable
data class AccessTokenResponse(
    val access_token: String
)