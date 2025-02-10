package org.company.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking


class SessionManager(private val settings: ObservableSettings) {
    var isLoggedIn by mutableStateOf(settings.getString("access_token", "").isNotEmpty())

    val flowAccessToken: Flow<String> = callbackFlow {
        val listener = settings.addStringListener(
            key = "access_token",
            defaultValue = "",
            callback = { token ->
                trySend(token)
            }
        )

        awaitClose {
            listener.deactivate()
        }
    }
    var accessToken = settings.getString("access_token", "")

    fun setToken(token: String) {
        accessToken = token
    }

    fun updateAccessToken(token: String) {
        isLoggedIn = true
        settings.putString("access_token", token)

        println("Updated access token. isLoggedIn = $isLoggedIn")
    }

    fun clearSession() {
        isLoggedIn = false
        settings.remove("access_token")
        println("Session cleared. isLoggedIn = $isLoggedIn")
    }
}
