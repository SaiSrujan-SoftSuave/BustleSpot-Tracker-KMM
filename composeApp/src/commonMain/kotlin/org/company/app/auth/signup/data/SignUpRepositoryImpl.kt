package org.company.app.auth.signup.data

import org.company.app.auth.utils.Result
import org.company.app.network.BASEURL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignUpRepositoryImpl(
    private val httpClient: HttpClient
) : SignUpRepository {
    override suspend fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Result<SignUpResponse>> =
        flow {
            try {
                val response: HttpResponse = httpClient.post("$BASEURL/auth/signup") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        SignUpRequest(
                            first_name = firstName,
                            last_name = lastName,
                            email = email,
                            password = password
                        )
                    )
                }

                if (response.status == HttpStatusCode.Created) {
                    val result: SignUpResponse = response.body()
                    println(result.message)
                    emit(Result.Success(result))
                } else {
                    emit(Result.Error("Failed to sign up: ${response.status}"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message))
            }
        }
}