package org.company.app

class AOSPlatform: Platform {
    override val name: String = "Android"
}
actual fun getPlatform(): Platform = AOSPlatform()