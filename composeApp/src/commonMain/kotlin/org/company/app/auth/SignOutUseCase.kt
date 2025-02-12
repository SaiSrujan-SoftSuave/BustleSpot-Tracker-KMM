package org.company.app.auth

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import org.company.app.MainViewModel
import org.company.app.auth.utils.Result
import org.company.app.network.APIEndpoints
import org.company.app.network.BASEURL
import org.company.app.network.models.response.SignOutResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.company.app.SessionManager

class SignOutUseCase(
    private val httpClient: HttpClient,
    private val settings: ObservableSettings,
    private val sessionManager: SessionManager
){
    operator fun invoke(): Flow<Result<SignOutResponseDto>> = flow {

        try {
            emit(Result.Loading)
            val response: HttpResponse = httpClient.post("$BASEURL${APIEndpoints.SIGNOUT}") {
                contentType(ContentType.Application.Json)
                bearerAuth(sessionManager.accessToken)
            }

            if (response.status == HttpStatusCode.OK) {
                val result: SignOutResponseDto = response.body() // Deserialize the response body
                emit(Result.Success(result))
                sessionManager.clearSession()
            } else {
                emit(Result.Error("Failed to sign out: ${response.status} ${response.body<Any>()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }
}