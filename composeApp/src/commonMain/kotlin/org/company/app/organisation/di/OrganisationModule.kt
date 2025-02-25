package org.company.app.organisation.di


import org.company.app.organisation.data.OrganisationRepository
import org.company.app.organisation.data.OrganisationRepositoryImpl
import org.company.app.organisation.ui.OrganisationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val organisationModule = module {
    single<OrganisationRepository> {
        OrganisationRepositoryImpl(get(),get())
    }

    viewModelOf(::OrganisationViewModel)
}
