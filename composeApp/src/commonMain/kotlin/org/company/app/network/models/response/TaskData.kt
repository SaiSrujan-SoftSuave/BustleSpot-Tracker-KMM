package org.company.app.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TaskData(
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("project_id")
    val projectId: String,
    @SerialName("status")
    val status: String,
    @SerialName("task_description")
    val taskDescription: String?,
    @SerialName("task_id")
    val taskId: String,
    @SerialName("task_name")
    val taskName: String
):DisplayItem()