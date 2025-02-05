package org.company.app

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences


actual fun createSettings(): Settings {
    val preferences = Preferences.userRoot().node("MyAppSettings")
    return PreferencesSettings(preferences)
}