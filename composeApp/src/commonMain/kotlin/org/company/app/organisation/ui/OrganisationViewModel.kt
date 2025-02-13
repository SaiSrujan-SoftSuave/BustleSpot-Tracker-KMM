package org.company.app.organisation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.company.app.auth.SignOutUseCase
import org.company.app.auth.utils.Result
import org.company.app.auth.utils.UiEvent
import org.company.app.network.APIEndpoints
import org.company.app.network.BASEURL
import org.company.app.network.models.response.GetAllOrganisations
import org.company.app.network.models.response.Organisation
import org.company.app.network.models.response.SignOutResponseDto
import org.company.app.organisation.data.OrganisationRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrganisationViewModel(
    private val organisationRepository: OrganisationRepository,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _organisationList : MutableStateFlow<GetAllOrganisations?> = MutableStateFlow(null)
    val organisationList: StateFlow<GetAllOrganisations?> = _organisationList.asStateFlow()

    private val _logOutEvent : MutableStateFlow<UiEvent<SignOutResponseDto>?> = MutableStateFlow(null)
    val logOutEvent : StateFlow<UiEvent<SignOutResponseDto>?> = _logOutEvent.asStateFlow()
    private val _uiEvent: MutableStateFlow<UiEvent<GetAllOrganisations>> = MutableStateFlow(UiEvent.Loading)
    val uiEvent: StateFlow<UiEvent<GetAllOrganisations>> = _uiEvent.asStateFlow()

    private val _showLogOutDialog :MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showLogOutDialog : StateFlow<Boolean> = _showLogOutDialog.asStateFlow()
    private fun getAllOrganisation() {
        viewModelScope.launch {

            organisationRepository.getAllOrganisation().collect { result ->
                when (result) {
                    is Result.Error -> {
                        _uiEvent.value = UiEvent.Failure(result.message ?: "Unknown Error")
                    }

                    Result.Loading -> {
                        _uiEvent.value = UiEvent.Loading
                    }

                    is Result.Success -> {
                        _organisationList.value = result.data
                        _uiEvent.value = UiEvent.Success(result.data)
                        println("Success: ${result.data}")
                    }
                }

            }
        }
    }

    fun logOutDisMissed(){
        _showLogOutDialog.value = false
    }
    fun showLogOutDialog(){
        _showLogOutDialog.value = true
    }
    fun performLogOut(){
        viewModelScope.launch {
            signOutUseCase.invoke().collect { result ->
                when (result) {
                    is Result.Error -> {
                        _logOutEvent.value = UiEvent.Failure(result.message ?: "Unknown Error")
                    }

                    Result.Loading -> {
                        _logOutEvent.value = UiEvent.Loading
                    }

                    is Result.Success -> {
                        _showLogOutDialog.value = false
                       val data = result.data
                        _logOutEvent.value = UiEvent.Success(data)
                        println("Success: ${result.data}")
                    }
                }

            }
        }
    }

    init {
        getAllOrganisation()
    }
}