package org.company.app.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ActivityResponseDto(
    @SerialName("activity_id")
    val activityId: String,
    @SerialName("message")
    val message: String
)