package org.company.app.tracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import bustlespot.composeapp.generated.resources.Res
import bustlespot.composeapp.generated.resources.ic_drop_down
import bustlespot.composeapp.generated.resources.ic_drop_up
import bustlespot.composeapp.generated.resources.ic_pause_circle
import bustlespot.composeapp.generated.resources.ic_play_arrow
import bustlespot.composeapp.generated.resources.screen
import kotlinx.coroutines.launch
import org.company.app.auth.utils.CustomAlertDialog
import org.company.app.auth.utils.LoadingScreen
import org.company.app.auth.utils.UiEvent
import org.company.app.auth.utils.secondsToTime
import org.company.app.auth.utils.secondsToTimeFormat
import org.company.app.network.models.response.DisplayItem
import org.company.app.network.models.response.Project
import org.company.app.network.models.response.TaskData
import org.company.app.organisation.ui.BustleSpotAppBar
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun TrackerScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    organisationName: String,
    onFocusReceived: () -> Unit = {}
) {
    val homeViewModel = koinViewModel<HomeViewModel>()

    // Tracker timer and other states from homeViewModel remain unchanged.
    val trackerTimer by homeViewModel.trackerTime.collectAsState()
    val isTrackerRunning by homeViewModel.isTrackerRunning.collectAsState()
    val idleTime by homeViewModel.idealTime.collectAsState()
    val screenShotState by homeViewModel.screenShotState.collectAsState()
    val keyCount by homeViewModel.keyboradKeyEvents.collectAsState()
    val mouseCount by homeViewModel.mouseKeyEvents.collectAsState()
    val screenShotTakenTime by homeViewModel.screenShotTakenTime.collectAsState()
    val customeTimeForIdleTime by homeViewModel.customeTimeForIdleTime.collectAsState()

    // Collect the consolidated drop-down states from HomeViewModel.
    val projectDropDownState by homeViewModel.projectDropDownState.collectAsState()
    val taskDropDownState by homeViewModel.taskDropDownState.collectAsState()

    // Still track the selected project and task if needed.
    val selectedProject by homeViewModel.selectedProject.collectAsState()
    val selectedTask by homeViewModel.selectedTask.collectAsState()

    // UI event (loading, failure, etc.) from the view model.
    val uiEvent by homeViewModel.uiEvent.collectAsState()

    // Local UI states.
    var showIdleDialog by remember { mutableStateOf(false) }
    var isExitClicked by remember { mutableStateOf(false) }
    var totalIdleTime by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Launch idle dialog effect.
    LaunchedEffect(idleTime) {
        if (idleTime > customeTimeForIdleTime && !showIdleDialog) {
            onFocusReceived.invoke()
            showIdleDialog = true
            homeViewModel.stopTrackerTimer()
            homeViewModel.updateTrackerTimer()
        }
    }

    // Launch fetching projects and tasks when the screen starts.
    LaunchedEffect(key1 = Unit) {
        homeViewModel.getAllProjects()
        homeViewModel.getAllTasks()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BustleSpotAppBar(
                title = { Text(text = organisationName) },
                onNavigationBackClick = {
                    if (isTrackerRunning) {
                        isExitClicked = true
                    } else {
                        navController.popBackStack()
                    }
                },
                isNavigationEnabled = true,
                isAppBarIconEnabled = false, // to remove the user icon in tracker screen
                iconUserName = "Test 1",
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Handle UI events (failure, loading, success).
            when (uiEvent) {
                is UiEvent.Failure -> {
                    LaunchedEffect(uiEvent) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                (uiEvent as UiEvent.Failure).error,
                                actionLabel = "Retry"
                            )
                        }
                    }
                    Text(
                        text = "Fetching data is failed due to ${(uiEvent as UiEvent.Failure).error}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is UiEvent.Loading -> {
                    LoadingScreen()
                }

                is UiEvent.Success -> {
                    DropDownSelectionList(
                        title = "Project",
                        dropDownList = projectDropDownState.dropDownList,
                        onItemClick = { selectedItem ->
                            homeViewModel.handleDropDownEvents(
                                DropDownEvents.OnProjectSelection(selectedItem as Project)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(vertical = 8.dp),
                        error = projectDropDownState.errorMessage,
                        onDropDownClick = {
                            homeViewModel.handleDropDownEvents(DropDownEvents.OnProjectDropDownClick)
                        },
                        inputText = projectDropDownState.inputText,
                        onSearchText = { searchText ->
                            homeViewModel.handleDropDownEvents(
                                DropDownEvents.OnProjectSearch(
                                    searchText
                                )
                            )
                        },
                        onNoOptionClick = {
                            homeViewModel.handleDropDownEvents(DropDownEvents.OnProjectSearch(""))
                        }
                    )


                    DropDownSelectionList(
                        title = "Task",
                        dropDownList = taskDropDownState.dropDownList,
                        onItemClick = { selectedItem ->
                            homeViewModel.handleDropDownEvents(
                                DropDownEvents.OnTaskSelection(selectedItem as TaskData)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(vertical = 8.dp),
                        error = taskDropDownState.errorMessage,
                        isEnabled = taskDropDownState.dropDownList.isNotEmpty(),
                        onDropDownClick = {
                            homeViewModel.handleDropDownEvents(DropDownEvents.OnTaskDropDownClick)
                        },
                        inputText = taskDropDownState.inputText,
                        onSearchText = { searchText ->
                            homeViewModel.handleDropDownEvents(
                                DropDownEvents.OnTaskSearch(
                                    searchText
                                )
                            )
                        },
                        onNoOptionClick = {
                            homeViewModel.handleDropDownEvents(DropDownEvents.OnTaskSearch(""))
                        }
                    )

                    TimerSessionSection(
                        trackerTimer = trackerTimer,
                        homeViewModel = homeViewModel,
                        keyCount = keyCount,
                        mouseCount = mouseCount,
                        idleTime = totalIdleTime,
                        isTrackerRunning = isTrackerRunning,
                        taskName = selectedTask?.taskName ?: ""
                    )

                    ScreenShotSection(
                        lastImageTakenTime = secondsToTime(screenShotTakenTime),
                        imageBitmap = screenShotState
                    )
                }
            }


            /*
                        Box {
                            Row {
                                TextField(
                                    value = customeTimeForIdleTime.toString(),
                                    onValueChange = {
                                        if (it.isNotEmpty()) {
                                            homeViewModel.addCustomTimeForIdleTime(it.toInt())
                                        } else {
                                            homeViewModel.addCustomTimeForIdleTime(10)
                                        }
                                    },
                                    label = { Text("Custom Time") },
                                )
                            }
                        }*/

            if (showIdleDialog) {
                CustomAlertDialog(
                    title = "IdleTime",
                    text = "You are idle for ${secondsToTime(idleTime)}. Do you want to add idle time to the session?",
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showIdleDialog = false
                                totalIdleTime += idleTime
                                homeViewModel.resetIdleTimer()
                                homeViewModel.resumeTrackerTimer()
                            },
                            colors = ButtonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(5.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 5.dp,
                                focusedElevation = 7.dp,
                            )
                        ) {
                            Text("Okay")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showIdleDialog = false
                                homeViewModel.resetIdleTimer()
                                homeViewModel.resumeTrackerTimer()
                            },
                            colors = ButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Red,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(5.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 5.dp,
                                focusedElevation = 7.dp,
                            )
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        if (isExitClicked) {
            CustomAlertDialog(
                title = "Quit",
                text = "Tracker is going to stop",
                confirmButton = {
                    TextButton(
                        onClick = {
                            isExitClicked = false
                            homeViewModel.stopTrackerTimer()
                            navController.popBackStack()
                        },
                        colors = ButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(5.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,
                            focusedElevation = 7.dp,
                        )
                    ) {
                        Text("Okay")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isExitClicked = false
                        },
                        colors = ButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Red,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(5.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,
                            focusedElevation = 7.dp,
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DropDownSelectionList(
    modifier: Modifier = Modifier,
    title: String,
    onSearchText: (String) -> Unit,
    inputText: String,
    dropDownList: List<DisplayItem>,
    onItemClick: (DisplayItem) -> Unit,
    isEnabled: Boolean = true,
    onDropDownClick: () -> Unit = {},
    onNoOptionClick: () -> Unit = {},
    error: String? = null,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    // We also track whether we've already notified the parent for this open.
    var hasNotifiedOnOpen by remember { mutableStateOf(false) }

    // Log state changes only when isMenuExpanded changes.
    LaunchedEffect(isMenuExpanded) {
        println("isMenuExpanded changed to: $isMenuExpanded")
    }

    // Memoize the filtered list based on the input text.
    val filteredList by remember(inputText, dropDownList) {
        derivedStateOf {
            dropDownList.filter {
                when (it) {
                    is Project -> it.projectName.contains(inputText, ignoreCase = true)
                    is TaskData -> it.taskName.contains(inputText, ignoreCase = true)
                    else -> true
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = inputText,
            onValueChange = {
                onSearchText(it)
                isMenuExpanded = true
                hasNotifiedOnOpen = true
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    onDropDownClick()
                    if (!isMenuExpanded && isEnabled) {
                        isMenuExpanded = true
                        // Only call onDropDownClick if we haven't already for this open cycle.
                        if (!hasNotifiedOnOpen) {
                            hasNotifiedOnOpen = true
                        }
                    } else {
                        // Close the dropdown and reset our notification flag.
                        isMenuExpanded = false
                        hasNotifiedOnOpen = false
                    }
                    println("icon clicked")
                }) {
                    Icon(
                        painter = painterResource(
                            if (isMenuExpanded) Res.drawable.ic_drop_up else Res.drawable.ic_drop_down
                        ),
                        contentDescription = "Toggle Dropdown"
                    )
                }
            },
            label = {
                Text(
                    text = title,
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            supportingText = {
                if (error?.isNotEmpty() == true) {
                    Text(text = error, color = Color.Red)
                }
            },
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Red
            ),
            isError = error?.isNotEmpty() ?: false
        )
        DropdownMenu(
            expanded = isMenuExpanded && hasNotifiedOnOpen && isEnabled,
            onDismissRequest = {
                isMenuExpanded = true
                hasNotifiedOnOpen = false
                println("dismiss called")
            },
            modifier = Modifier.fillMaxWidth(0.85f),
            properties = PopupProperties(focusable = false)
        ) {
            if (filteredList.isNotEmpty()) {
                filteredList.forEach { item ->
                    when (item) {
                        is Project -> {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item.projectName,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    onItemClick(item)
                                }
                            )
                        }

                        is TaskData -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = item.taskName, modifier = Modifier.fillMaxWidth())
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    onItemClick(item)
                                }
                            )
                        }
                    }
                }
            } else {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "No Options",
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Gray
                        )
                    },
                    onClick = {
                        isMenuExpanded = false
                        onNoOptionClick()
                    },
                )
            }
        }
    }
}


@Composable
fun TimerSessionSection(
    modifier: Modifier = Modifier,
    taskName: String = "task",
    trackerTimer: Int,
    homeViewModel: HomeViewModel,
    idleTime: Int,
    mouseCount: Int,
    keyCount: Int,
    isTrackerRunning: Boolean,
) {
    var isPlaying by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth(0.85f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Current Session",
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPlaying) taskName else "",
                color = Color.Red,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        isPlaying = !isPlaying
                        if (isPlaying) {
                            if (isTrackerRunning || homeViewModel.trackerTime.value != 0) {
                                homeViewModel.resumeTrackerTimer()
                            } else {
                                homeViewModel.startTrackerTimer()
                            }
                        } else {
                            if (isTrackerRunning) {
                                homeViewModel.stopTrackerTimer()
                            } else {
                                homeViewModel.resetTrackerTimer()
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (isPlaying) Res.drawable.ic_pause_circle else Res.drawable.ic_play_arrow
                        ),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(24.dp),
                    )
                }
                Text(
                    text = secondsToTime(trackerTimer),
                    color = Color.Black,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "IdleTime",
                color = Color.Red,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = secondsToTimeFormat(idleTime),
                color = Color.Black,
            )
        }
        /*        Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Key Events",
                        color = Color.Red,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$keyCount",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mouse Events",
                        color = Color.Red,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$mouseCount",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }*/
    }
}

@Composable
fun ScreenShotSection(
    modifier: Modifier = Modifier,
    lastImageTakenTime: String = "10min ago",
    imageBitmap: ImageBitmap? = imageResource(Res.drawable.screen)
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Latest Screen Capture",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = lastImageTakenTime,
                color = Color.Red,
                style = MaterialTheme.typography.labelSmall
            )
        }
        imageBitmap?.let { bitmap ->
            Image(
                modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally),
                bitmap = bitmap,
                contentDescription = "Screenshot"
            )
        }
    }
}
