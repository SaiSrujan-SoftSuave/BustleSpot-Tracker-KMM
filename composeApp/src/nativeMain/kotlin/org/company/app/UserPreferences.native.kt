package org.company.app

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

actual fun createSettings(): ObservableSettings {
    val delegate: NSUserDefaults  = NSUserDefaults.standardUserDefaults
    return NSUserDefaultsSettings(delegate)
}