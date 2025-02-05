package org.company.app.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    @SerialName("is_delected")
    val isDelected: Boolean,
    @SerialName("organisation_id")
    val organisationId: String,
    @SerialName("project_id")
    val projectId: String,
    @SerialName("project_name")
    val projectName: String
): DisplayItem()

open class DisplayItem {

}
