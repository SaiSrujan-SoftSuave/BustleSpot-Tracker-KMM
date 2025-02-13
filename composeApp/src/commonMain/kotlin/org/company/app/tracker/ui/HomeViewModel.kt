package org.company.app.tracker.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.company.app.auth.utils.Result
import org.company.app.auth.utils.UiEvent
import org.company.app.network.models.response.DisplayItem
import org.company.app.network.models.response.Project
import org.company.app.network.models.response.TaskData
import org.company.app.timer.TrackerModule
import org.company.app.tracker.data.TrackerRepository

class HomeViewModel(
    private val trackerRepository: TrackerRepository
) : ViewModel() {


    private val trackerModule = TrackerModule(viewModelScope)

    val trackerTime: StateFlow<Int> = trackerModule.trackerTime
    val isTrackerRunning: StateFlow<Boolean> = trackerModule.isTrackerRunning
    val idealTime: StateFlow<Int> = trackerModule.idealTime
    val screenShotTakenTime: StateFlow<Int> = trackerModule.screenShotTakenTime
    val keyboradKeyEvents: StateFlow<Int> = trackerModule.keyboradKeyEvents
    val mouseKeyEvents: StateFlow<Int> = trackerModule.mouseKeyEvents
    val mouseMotionCount: StateFlow<Int> = trackerModule.mouseMotionCount
    val customeTimeForIdleTime: StateFlow<Int> = trackerModule.customeTimeForIdleTime
    val screenShotState: StateFlow<ImageBitmap?> = trackerModule.screenShotState

    fun startTrackerTimer() = trackerModule.startTimer()
    fun stopTrackerTimer() = trackerModule.stopTimer()
    fun resetTrackerTimer() = trackerModule.resetTimer()
    fun resumeTrackerTimer() = trackerModule.resumeTracker()

    fun startScreenshotTask() = trackerModule.startScreenshotTask()
    fun pauseScreenshotTask() = trackerModule.pauseScreenshotTask()
    fun resumeScreenshotTask() = trackerModule.resumeScreenshotTask()
    fun stopScreenshotTask() = trackerModule.stopScreenshotTask()

    fun resetIdleTimer() = trackerModule.resetIdleTimer()
    fun updateTrackerTimer() = trackerModule.updateTrackerTimer()
    fun addCustomTimeForIdleTime(time: Int) = trackerModule.addCustomTimeForIdleTime(time)


    private val _taskList = kotlinx.coroutines.flow.MutableStateFlow<List<TaskData>>(emptyList())
    private val _mainTaskList =
        kotlinx.coroutines.flow.MutableStateFlow<List<TaskData>>(emptyList())
    private val _mainProjectList =
        kotlinx.coroutines.flow.MutableStateFlow<List<Project>>(emptyList())

    private val _uiEvent =
        kotlinx.coroutines.flow.MutableStateFlow<UiEvent<TrackerScreenData>>(UiEvent.Loading)
    val uiEvent: StateFlow<UiEvent<TrackerScreenData>> get() = _uiEvent

    private val trackerScreenData = TrackerScreenData(mutableListOf(), mutableListOf())

    private val _selectedProject = kotlinx.coroutines.flow.MutableStateFlow<Project?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProject

    private val _selectedTask = kotlinx.coroutines.flow.MutableStateFlow<TaskData?>(null)
    val selectedTask: StateFlow<TaskData?> = _selectedTask

    private val _projectDropDownState = kotlinx.coroutines.flow.MutableStateFlow(DropDownState())
    val projectDropDownState: StateFlow<DropDownState> = _projectDropDownState

    private val _taskDropDownState = kotlinx.coroutines.flow.MutableStateFlow(DropDownState())
    val taskDropDownState: StateFlow<DropDownState> = _taskDropDownState

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
                            errorMessage = if (result.data.projectsData.projectList.isEmpty()) "No projects to select" else ""
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
                    _taskDropDownState.value =
                        _taskDropDownState.value.copy(dropDownList = emptyList())
                }
            }

            is DropDownEvents.OnProjectSelection -> {
                _selectedProject.value = dropDownEvents.selectedProject
                _projectDropDownState.value = _projectDropDownState.value.copy(
                    inputText = dropDownEvents.selectedProject.projectName,
                    errorMessage = ""
                )

                val filteredTasks =
                    _mainTaskList.value.filter { it.projectId == dropDownEvents.selectedProject.projectId }
                _taskDropDownState.value = _taskDropDownState.value.copy(
                    dropDownList = filteredTasks,
                    errorMessage = if (filteredTasks.isEmpty()) "No task available to select" else "",
                    inputText = ""
                )
                if (filteredTasks.isEmpty()) {
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
                    _projectDropDownState.value =
                        _projectDropDownState.value.copy(errorMessage = "")
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
    object OnProjectDropDownClick : DropDownEvents()
    object OnTaskDropDownClick : DropDownEvents()
}

data class DropDownState(
    val errorMessage: String = "",
    val inputText: String = "",
    val dropDownList: List<DisplayItem> = emptyList()
)
