package org.company.app.tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.company.app.auth.utils.Result
import org.company.app.auth.utils.UiEvent
import org.company.app.network.models.response.DisplayItem
import org.company.app.network.models.response.Project
import org.company.app.network.models.response.TaskData
import org.company.app.tracker.data.TrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val trackerRepository: TrackerRepository
) : ViewModel() {

    private val _projectList: MutableStateFlow<List<Project>> = MutableStateFlow(emptyList())
    val projectList: StateFlow<List<DisplayItem>> get() = _projectList.asStateFlow()

    private val _taskList: MutableStateFlow<List<TaskData>> = MutableStateFlow(emptyList())
    val taskList: StateFlow<List<DisplayItem>> get() = _taskList.asStateFlow()

    private val _uiEvent: MutableStateFlow<UiEvent<TrackerScreenData>> =
        MutableStateFlow(UiEvent.Loading)
    val uiEvent: StateFlow<UiEvent<TrackerScreenData>> get() = _uiEvent.asStateFlow()

    private val trackerScreenData: TrackerScreenData = TrackerScreenData(null, null)

    private fun getAllProjects() {
        viewModelScope.launch {
            trackerRepository.getAllProjects().collect { result ->
                when (result) {
                    is Result.Error -> {
                        _uiEvent.value = UiEvent.Failure(result.message ?: "Unknown Error")
                    }

                    Result.Loading -> {
                        _uiEvent.value = UiEvent.Loading
                    }

                    is Result.Success -> {
                        _projectList.value = result.data.projectsData.projectList
                        trackerScreenData.listOfProject?.addAll(result.data.projectsData.projectList)
                        _uiEvent.value = UiEvent.Success(trackerScreenData)
                        println("Success: ${result.data}")
                    }
                }

            }
        }
    }

    private fun getAllTasks() {
        viewModelScope.launch {
            trackerRepository.getAllTask().collect { result ->
                when (result) {
                    is Result.Error -> {
                        _uiEvent.value = UiEvent.Failure(result.message ?: "Unknown Error")
                    }

                    Result.Loading -> {
                        _uiEvent.value = UiEvent.Loading
                    }

                    is Result.Success -> {
                        _taskList.value = result.data.taskList
                        trackerScreenData.listOfTask?.addAll(result.data.taskList)
                        _uiEvent.value = UiEvent.Success(trackerScreenData)
                        println("Success: ${result.data}")
                    }
                }

            }
        }
    }

    init {
        getAllProjects()
        getAllTasks()
    }

}

data class TrackerScreenData(
    val listOfProject: MutableList<Project>?,
    val listOfTask: MutableList<TaskData>?
)