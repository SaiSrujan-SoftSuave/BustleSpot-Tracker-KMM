package org.company.app

import android.content.Context
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.context.GlobalContext

actual fun createSettings(): ObservableSettings {
    val context: Context = GlobalContext.get().get<Context>()
    val delegate = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(delegate)
}