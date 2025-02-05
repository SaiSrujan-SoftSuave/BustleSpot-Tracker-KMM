package org.company.app.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Organisation(
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("organisation_description")
    val organisationDescription: String,
    @SerialName("organisation_id")
    val organisationId: String,
    @SerialName("organisation_name")
    val organisationName: String
)