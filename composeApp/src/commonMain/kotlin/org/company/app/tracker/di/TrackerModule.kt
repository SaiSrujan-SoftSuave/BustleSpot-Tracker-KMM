package org.company.app.tracker.di

import org.company.app.tracker.data.TrackerRepository
import org.company.app.tracker.data.TrackerRepositoryImpl
import org.company.app.tracker.ui.HomeViewModel
import org.koin.dsl.module

val trackerModule = module{
    single<TrackerRepository>{
        TrackerRepositoryImpl(get())
    }
    single {
        HomeViewModel(get())
    }
}