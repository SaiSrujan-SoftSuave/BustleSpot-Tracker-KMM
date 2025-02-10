package org.company.app.di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.company.app.MainViewModel
import org.company.app.SessionManager
import org.company.app.auth.signin.data.AccessTokenResponse
import org.company.app.createSettings
import org.company.app.getEngine
import org.company.app.network.BASEURL
import org.company.app.tracker.di.trackerModule
import org.koin.dsl.module

val koinGlobalModule = module {
    single { MainViewModel(get()) { provideUnauthenticatedHttpClient() } }
    factory { provideHttpClient(get(),get()) }
    trackerModule
    single<ObservableSettings> {
        createSettings()
    }
}

fun provideUnauthenticatedHttpClient(): HttpClient {
    return HttpClient(getEngine()) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

fun provideHttpClient(settings: ObservableSettings, sessionManager: SessionManager): HttpClient {
    return HttpClient(getEngine()) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }


        install(HttpCallValidator) {
            validateResponse { response ->
                if (response.status.value == 401) {
                    println("Received 401: Forcing logout")
                    sessionManager.clearSession()
                }
            }
        }

        if (settings.getString("access_token", "").isNotEmpty()) {
            install(Auth) {
                bearer {
//                    loadTokens {
//                        BearerTokens(
//                            accessToken =settings.getString("access_token",""),
//                            refreshToken = sessionManager.accessToken
//                        )
//                    }

                    refreshTokens {
                        try {
                            val response: HttpResponse = client.get("$BASEURL/auth/refresh-token") {
                                bearerAuth(sessionManager.accessToken ?: return@refreshTokens null)
                            }

                            if (response.status.isSuccess()) {
                                val newAccessToken = response.body<AccessTokenResponse>().access_token
                                settings.putString("access_token", newAccessToken)
                                sessionManager.updateAccessToken(newAccessToken)

                                return@refreshTokens BearerTokens(newAccessToken, sessionManager.accessToken)
                            }
                        } catch (e: Exception) {
                            println("Token refresh failed: ${e.message}")
                        }
                        return@refreshTokens null
                    }
                }
            }
        }
    }
}
