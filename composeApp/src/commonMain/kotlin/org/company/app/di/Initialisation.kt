package org.company.app.di

import org.company.app.auth.di.platformModule
import org.company.app.auth.di.sharedModules
import org.company.app.organisation.di.organisationModule
import org.company.app.tracker.di.trackerModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(koinGlobalModule,sharedModules, platformModule,organisationModule,trackerModule)
    }
}