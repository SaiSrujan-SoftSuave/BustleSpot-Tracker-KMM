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
import kotlinx.coroutines.launch

class OrganisationViewModel(
    private val organisationRepository: OrganisationRepository,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _organisationList : MutableStateFlow<GetAllOrganisations?> = MutableStateFlow(null)
    val organisationList: StateFlow<GetAllOrganisations?> = _organisationList


    private val _uiEvent: MutableStateFlow<UiEvent<GetAllOrganisations>?> = MutableStateFlow(null)
    val uiEvent: StateFlow<UiEvent<GetAllOrganisations>?> = _uiEvent


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
                        _organisationList.value = result.data.copy(isLoggingOut = false)
                        _uiEvent.value = UiEvent.Success(result.data.copy(isLoggingOut = false))
                        println("Success: ${result.data}")
                    }
                }

            }
        }
    }

    fun performLogOut(){
        viewModelScope.launch {
            signOutUseCase.invoke().collect { result ->
                when (result) {
                    is Result.Error -> {
                        _uiEvent.value = UiEvent.Failure(result.message ?: "Unknown Error")
                    }

                    Result.Loading -> {
                        _uiEvent.value = UiEvent.Loading
                    }

                    is Result.Success -> {
                        val data = result.data
                        _uiEvent.value = UiEvent.Success(GetAllOrganisations(
                            listOfOrganisations = emptyList(),
                            message = data.message,
                            isLoggingOut = true
                        ))
                        println("Success: ${result.data}")
                    }
                }

            }
        }
    }
    fun clearUiEvent() {
        _uiEvent.value = UiEvent.Success(_organisationList.value?.copy(isLoggingOut = false)!!)
    }

    init {
        getAllOrganisation()
    }
}
//
//sealed class OrgansitionActions{
//    data object SignOut : OrgansitionActions
//
//}