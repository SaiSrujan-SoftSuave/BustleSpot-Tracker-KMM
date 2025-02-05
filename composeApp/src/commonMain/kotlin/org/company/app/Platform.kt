package org.company.app
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform