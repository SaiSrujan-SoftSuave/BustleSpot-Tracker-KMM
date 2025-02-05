package org.company.app.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import org.company.app.MainViewModel
import org.company.app.auth.SignOutUseCase
import org.company.app.auth.signin.data.AccessTokenResponse
import org.company.app.createSettings
import org.company.app.getEngine
import org.company.app.network.BASEURL
import org.company.app.tracker.di.trackerModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val koinGlobalModule = module {
    single { MainViewModel(get()) { provideUnauthenticatedHttpClient() } }
    single { provideHttpClient(get()) }
    trackerModule
    single<Settings> {
        createSettings()
    }
}

fun provideUnauthenticatedHttpClient(): HttpClient {
    return HttpClient(getEngine()) {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

fun provideHttpClient(settings: Settings): HttpClient {
    return HttpClient(getEngine()) {
        install(Logging) { level = LogLevel.ALL }

        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        if (settings.getString("access_token", "").isNotEmpty()) {
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = settings["access_token", ""],
                            refreshToken = settings["refresh_token", ""]
                        )
                    }

                    refreshTokens {
                        try {
                            val response: HttpResponse = client.get("$BASEURL/auth/refresh-token") {
                                bearerAuth(
                                    settings.get("access_token", "") ?: return@refreshTokens null
                                )
                            }

                            if (response.status.isSuccess()) {
                                val newAccessToken =
                                    response.body<AccessTokenResponse>().access_token
                                settings.putString("access_token", newAccessToken)

                                return@refreshTokens settings.get("access_token", "")
                                    .let { BearerTokens(newAccessToken, it) }
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
