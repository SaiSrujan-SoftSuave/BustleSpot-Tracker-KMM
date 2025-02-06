package org.company.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings

class SessionManager(private val settings: Settings) {
    var isLoggedIn by mutableStateOf(settings.getString("access_token", "").isNotEmpty())

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
