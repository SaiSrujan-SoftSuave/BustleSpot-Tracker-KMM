package org.company.app.organisation.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import org.company.app.MainViewModel
import org.company.app.auth.utils.Result
import org.company.app.network.APIEndpoints.GETALLORGANISATIONS
import org.company.app.network.BASEURL
import org.company.app.network.models.response.ErrorResponse
import org.company.app.network.models.response.GetAllOrganisations
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.company.app.SessionManager

class OrganisationRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionManager: SessionManager,
    private val mainViewModel: MainViewModel,
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
