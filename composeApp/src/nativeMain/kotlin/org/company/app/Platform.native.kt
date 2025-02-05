package org.company.app

class IOSPlatform: Platform {
    override val name: String = "IOS"
}
actual fun getPlatform(): Platform = IOSPlatform()