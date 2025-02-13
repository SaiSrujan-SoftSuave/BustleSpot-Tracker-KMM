package org.company.app.tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.company.app.auth.utils.Result
import org.company.app.auth.utils.UiEvent
import org.company.app.network.models.response.DisplayItem
import org.company.app.network.models.response.Project
import org.company.app.network.models.response.TaskData
import org.company.app.tracker.data.TrackerRepository

class HomeViewModel(
    private val trackerRepository: TrackerRepository
) : ViewModel() {

    private val _taskList: MutableStateFlow<List<TaskData>> = MutableStateFlow(emptyList())

    private val _mainTaskList: MutableStateFlow<List<TaskData>> = MutableStateFlow(emptyList())
    private val _mainProjectList: MutableStateFlow<List<Project>> = MutableStateFlow(emptyList())

    private val _uiEvent: MutableStateFlow<UiEvent<TrackerScreenData>> =
        MutableStateFlow(UiEvent.Loading)
    val uiEvent: StateFlow<UiEvent<TrackerScreenData>> get() = _uiEvent.asStateFlow()

    private val trackerScreenData: TrackerScreenData = TrackerScreenData(null, null)

    private val _selectedProject: MutableStateFlow<Project?> = MutableStateFlow(null)
    val selectedProject: StateFlow<Project?> = _selectedProject.asStateFlow()

    private val _selectedTask: MutableStateFlow<TaskData?> = MutableStateFlow(null)
    val selectedTask: StateFlow<TaskData?> = _selectedTask.asStateFlow()

    private val _projectDropDownState: MutableStateFlow<DropDownState> =
        MutableStateFlow(DropDownState())
    val projectDropDownState: StateFlow<DropDownState> = _projectDropDownState.asStateFlow()

    private val _taskDropDownState: MutableStateFlow<DropDownState> =
        MutableStateFlow(DropDownState())
    val taskDropDownState: StateFlow<DropDownState> = _taskDropDownState.asStateFlow()

    fun getAllProjects() {
        viewModelScope.launch {
            trackerRepository.getAllProjects().collect { result ->
                when (result) {
                    is Result.Error -> {
                        _uiEvent.value = UiEvent.Failure(result.message ?: "Unknown Error")
                        _projectDropDownState.value = _projectDropDownState.value.copy(
                            errorMessage = result.message ?: "Failed to fetch projects"
                        )
                    }

                    Result.Loading -> {
                        _uiEvent.value = UiEvent.Loading
                    }

                    is Result.Success -> {
                        _mainProjectList.value = result.data.projectsData.projectList
                        _projectDropDownState.value = _projectDropDownState.value.copy(
                            dropDownList = result.data.projectsData.projectList,
                            errorMessage = if(result.data.projectsData.projectList.isEmpty()) "No projects to select" else ""
                        )
                        trackerScreenData.listOfProject?.addAll(result.data.projectsData.projectList)
                        _uiEvent.value = UiEvent.Success(trackerScreenData)
                    }
                }
            }
        }
    }

    fun getAllTasks() {
        viewModelScope.launch {
            trackerRepository.getAllTask().collect { result ->
                when (result) {
                    is Result.Error -> {
                        _uiEvent.value = UiEvent.Failure(result.message ?: "Unknown Error")
                        _taskDropDownState.value = _taskDropDownState.value.copy(
                            errorMessage = result.message ?: "Failed to fetch tasks"
                        )
                    }

                    Result.Loading -> {
                        _uiEvent.value = UiEvent.Loading
                    }

                    is Result.Success -> {
                        _mainTaskList.value = result.data.taskList
                        _taskDropDownState.value = _taskDropDownState.value.copy(
                            dropDownList = _taskList.value,
                            errorMessage = ""
                        )
                        trackerScreenData.listOfTask?.addAll(result.data.taskList)
                        _uiEvent.value = UiEvent.Success(trackerScreenData)
                    }
                }
            }
        }
    }

    fun handleDropDownEvents(dropDownEvents: DropDownEvents) {
        when (dropDownEvents) {
            is DropDownEvents.OnProjectSearch -> {
                _projectDropDownState.value = _projectDropDownState.value.copy(
                    inputText = dropDownEvents.inputText
                )
                if (dropDownEvents.inputText.isEmpty()) {
                    _selectedProject.value = null
                    _taskDropDownState.value = _taskDropDownState.value.copy(dropDownList = emptyList())
                }
            }

            is DropDownEvents.OnProjectSelection -> {
                _selectedProject.value = dropDownEvents.selectedProject
                _projectDropDownState.value = _projectDropDownState.value.copy(
                    inputText = dropDownEvents.selectedProject.projectName,
                    errorMessage = ""
                )

                val filteredTasks = _mainTaskList.value.filter { it.projectId == dropDownEvents.selectedProject.projectId }
                _taskDropDownState.value = _taskDropDownState.value.copy(
                    dropDownList = filteredTasks,
                    errorMessage = if (filteredTasks.isEmpty()) {
                        "No task available to select"
                    } else "",
                    inputText = ""
                )
                if(filteredTasks.isEmpty()){
                    _selectedTask.value = null
                }
            }

            is DropDownEvents.OnTaskSearch -> {
                _taskDropDownState.value = _taskDropDownState.value.copy(
                    inputText = dropDownEvents.inputText
                )
                if (dropDownEvents.inputText.isEmpty()) {
                    _selectedTask.value = null
                }
                if (_selectedProject.value == null) {
                    _projectDropDownState.value = _projectDropDownState.value.copy(
                        errorMessage = "Please select the project first"
                    )
                }
            }

            is DropDownEvents.OnTaskSelection -> {
                _selectedTask.value = dropDownEvents.selectedTask
                _taskDropDownState.value = _taskDropDownState.value.copy(
                    inputText = dropDownEvents.selectedTask.taskName,
                    errorMessage = ""
                )
            }

            is DropDownEvents.OnProjectDropDownClick -> {
                println("Project dropdown clicked")
            }

            is DropDownEvents.OnTaskDropDownClick -> {
                if (_selectedProject.value == null) {
                    _projectDropDownState.value = _projectDropDownState.value.copy(
                        errorMessage = "Please select the project first"
                    )
                } else {
                    _projectDropDownState.value = _projectDropDownState.value.copy(errorMessage = "")
                }
            }
        }
    }
}

data class TrackerScreenData(
    val listOfProject: MutableList<Project>?,
    val listOfTask: MutableList<TaskData>?
)

sealed class DropDownEvents {
    data class OnProjectSearch(val inputText: String) : DropDownEvents()
    data class OnTaskSearch(val inputText: String) : DropDownEvents()

    data class OnProjectSelection(val selectedProject: Project) : DropDownEvents()
    data class OnTaskSelection(val selectedTask: TaskData) : DropDownEvents()

    data object OnProjectDropDownClick : DropDownEvents()
    data object OnTaskDropDownClick : DropDownEvents()
}

data class DropDownState(
    val errorMessage: String = "",
    val inputText: String = "",
    val dropDownList: List<DisplayItem> = emptyList(),
)

sealed class TimerEvents{
    data object onTimerStart: TimerEvents()
}