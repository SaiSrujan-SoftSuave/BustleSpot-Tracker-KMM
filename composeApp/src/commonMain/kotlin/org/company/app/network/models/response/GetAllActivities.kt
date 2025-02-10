package org.company.app.network.models.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAllActivities(
    @SerialName("activity")
    val activity: List<Activity>
)