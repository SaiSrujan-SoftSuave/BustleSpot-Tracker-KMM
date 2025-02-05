package org.company.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttpEngine

actual fun getEngine(): HttpClientEngine {
    return CIO.create()
}