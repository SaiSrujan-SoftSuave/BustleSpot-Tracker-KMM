package org.company.app.network.models.request



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityDto(
    @SerialName("date")
    val date: String,
    @SerialName("idle_time")
    val idleTime: String,
    @SerialName("keyboard")
    val keyboard: String,
    @SerialName("mouse")
    val mouse: String,
    @SerialName("task_id")
    val taskId: String,
    @SerialName("total_time")
    val totalTime: String,
    @SerialName("working_time")
    val workingTime: String
)