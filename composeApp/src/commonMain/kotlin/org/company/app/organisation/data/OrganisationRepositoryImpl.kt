package org.company.app.organisation.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.company.app.SessionManager
import org.company.app.auth.utils.Result
import org.company.app.network.APIEndpoints.GETALLORGANISATIONS
import org.company.app.network.BASEURL
import org.company.app.network.models.response.ErrorResponse
import org.company.app.network.models.response.GetAllOrganisations

class OrganisationRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionManager: SessionManager,
) : OrganisationRepository {
    override fun getAllOrganisation(): Flow<Result<GetAllOrganisations>> = flow {
        try {
            emit(Result.Loading)
            val response: HttpResponse = httpClient.get("$BASEURL$GETALLORGANISATIONS") {
                contentType(ContentType.Application.Json)
                bearerAuth(sessionManager.accessToken)
            }


            if (response.status == HttpStatusCode.OK) {
                val result: GetAllOrganisations = response.body()
                emit(Result.Success(result))
            } else {
                val res: ErrorResponse = response.body()
                println(res)
                emit(Result.Error("Failed to fetch organisations: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }
}
