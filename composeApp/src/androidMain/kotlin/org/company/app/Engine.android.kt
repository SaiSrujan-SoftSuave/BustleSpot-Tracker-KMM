package org.company.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp


actual fun getEngine(): HttpClientEngine = OkHttp.create()