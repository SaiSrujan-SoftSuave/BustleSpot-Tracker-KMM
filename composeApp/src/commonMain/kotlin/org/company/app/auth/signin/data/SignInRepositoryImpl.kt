package org.company.app.auth.signin.data

import org.company.app.auth.utils.Result
import org.company.app.network.APIEndpoints
import org.company.app.network.BASEURL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SignInRepositoryImpl(
    private val httpClient: HttpClient
) : SignInRepository {

    override suspend fun testRoot(): Flow<Result<Any>> = flow {
        try {
            val response: HttpResponse = httpClient.get(BASEURL) {
                contentType(ContentType.Application.Json)
            }
            if (response.status == HttpStatusCode.OK) {
                val result = response.body<String>()
                emit(Result.Success(result))
                println("Success: $result")
            } else {
                emit(Result.Error("Failed to sign in: ${response.status}"))
                println("Failed to sign in: ${response.status}")
            }
        } catch (e: Exception) {
            emit(Result.Error("Error occurred: ${e.message}"))
            println("Error: ${e.message}")
        }
    }

    override fun signIn(email: String, password: String): Flow<Result<SignInResponse>> = flow {
        try {
            emit(Result.Loading)
            val response: HttpResponse = httpClient.post("$BASEURL${APIEndpoints.SIGNIN}") {
                contentType(ContentType.Application.Json)
                setBody(
                    SignInRequest(
                        email = email,
                        password = password
                    )
                )
            }

            if (response.status == HttpStatusCode.OK) {
                val result: SignInResponse = response.body() // Deserialize the response body
                emit(Result.Success(result))
            } else {
                emit(Result.Error("Failed to sign in: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }
}