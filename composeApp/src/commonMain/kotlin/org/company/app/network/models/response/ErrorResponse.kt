package org.company.app.network.models.response



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("error_code")
    val errorCode: String,
    @SerialName("message")
    val message: String
)