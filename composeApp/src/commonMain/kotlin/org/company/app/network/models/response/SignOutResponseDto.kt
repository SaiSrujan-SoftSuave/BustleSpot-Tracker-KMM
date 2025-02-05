package org.company.app.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//{
//    "message": "Log out successful"
//}
@Serializable
data class SignOutResponseDto(
    @SerialName("message")
    val message: String
)
