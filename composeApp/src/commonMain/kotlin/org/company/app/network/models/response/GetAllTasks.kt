package org.company.app.network.models.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAllTasks(
    @SerialName("data")
    val taskList: List<TaskData>,
    @SerialName("message")
    val message: String
)