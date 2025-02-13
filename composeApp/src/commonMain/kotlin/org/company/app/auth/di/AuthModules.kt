package org.company.app.auth.di


import org.company.app.auth.SignOutUseCase
import org.company.app.auth.signin.data.SignInRepository
import org.company.app.auth.signin.data.SignInRepositoryImpl
import org.company.app.auth.signin.presentation.LoginViewModel
import org.company.app.auth.signup.data.SignUpRepository
import org.company.app.auth.signup.data.SignUpRepositoryImpl
import org.company.app.di.provideHttpClient
import org.company.app.tracker.ui.HomeViewModelForTimer
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


expect val platformModule: Module


val sharedModules = module {
//    viewModelFactory {  }
//    viewModel { LoginViewModel(get(),get()) } // If dependencies are needed, use get()
//    viewModel { HomeViewModelForTimer(get()) }
//    viewModel { TrackerViewModel(get()) }
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModelForTimer)


    single {
        provideHttpClient(
            get(), get()
        )
    }
    single { org.company.app.SessionManager(get()) }
    single<SignInRepository> { SignInRepositoryImpl(get()) }
    single<SignUpRepository> { SignUpRepositoryImpl(get()) }

    single<SignOutUseCase> {
        SignOutUseCase(get(), get(), get())
    }
}

