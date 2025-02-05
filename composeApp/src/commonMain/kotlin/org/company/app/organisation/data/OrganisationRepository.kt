package org.company.app.organisation.data

import org.company.app.auth.utils.Result
import org.company.app.network.models.response.GetAllOrganisations
import org.company.app.network.models.response.Organisation
import kotlinx.coroutines.flow.Flow

fun interface OrganisationRepository {
    fun getAllOrganisation(): Flow<Result<GetAllOrganisations>>
}