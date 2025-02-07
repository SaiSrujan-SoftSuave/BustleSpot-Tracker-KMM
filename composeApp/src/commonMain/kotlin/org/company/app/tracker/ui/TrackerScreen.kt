package org.company.app.tracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import compose_multiplatform_app.composeapp.generated.resources.Res
import compose_multiplatform_app.composeapp.generated.resources.ic_drop_down
import compose_multiplatform_app.composeapp.generated.resources.ic_drop_up
import compose_multiplatform_app.composeapp.generated.resources.ic_pause_circle
import compose_multiplatform_app.composeapp.generated.resources.ic_play_arrow
import compose_multiplatform_app.composeapp.generated.resources.screen
import kotlinx.coroutines.launch
import org.company.app.MainViewModel
import org.company.app.SessionManager
import org.company.app.auth.utils.CustomAlertDialog
import org.company.app.auth.utils.LoadingScreen
import org.company.app.auth.utils.Result
import org.company.app.auth.utils.UiEvent
import org.company.app.auth.utils.secondsToTime
import org.company.app.network.models.response.DisplayItem
import org.company.app.network.models.response.Project
import org.company.app.network.models.response.TaskData
import org.company.app.organisation.ui.BustleSpotAppBar
import org.company.app.timer.TrackerViewModel
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
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
    val trackerViewModel = koinViewModel<TrackerViewModel>()

    // States from view models.
    val trackerTimer by trackerViewModel.trackerTime.collectAsState()
    val isTrackerRunning by trackerViewModel.isTrackerRunning.collectAsState()
    val idleTime by trackerViewModel.idealTime.collectAsState()
    val isIdleRunning by trackerViewModel.isIdealTimerRunning.collectAsState()
    val screenShotState by trackerViewModel.screenShotState.collectAsState()
    val keyCount by trackerViewModel.keyboradKeyEvents.collectAsState()
    val mouseCount by trackerViewModel.mouseKeyEvents.collectAsState()
    val screenShotTakenTime by trackerViewModel.screenShotTakenTime.collectAsState()
    val customeTimeForIdleTime by trackerViewModel.customeTimeForIdleTime.collectAsState()
    val numberOfScreenshot by trackerViewModel.numberOfScreenshot.collectAsState()

    // UI and local states
    var showIdleDialog by remember { mutableStateOf(false) }
    var isExitClicked by remember { mutableStateOf(false) }
    var totalIdleTime by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val sessionManager: SessionManager = koinInject()

    // Trigger idle dialog only when needed.
    LaunchedEffect(idleTime) {
        if (idleTime > customeTimeForIdleTime && !showIdleDialog) {
            onFocusReceived.invoke()
            showIdleDialog = true
            trackerViewModel.stopTimer()
            trackerViewModel.updateTrackerTimer()
        }
    }

    // UI events handling (snackbar, loading)
    val uiEvent by homeViewModel.uiEvent.collectAsState()
    val projectsList by homeViewModel.projectList.collectAsState()
    val tasksList by homeViewModel.taskList.collectAsState()

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
                isAppBarIconEnabled = true,
                iconUserName = "Test 1",
                onLogOutClick = {
                    // no need to implement in tracker screen
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                }

                is UiEvent.Loading -> {
                    LoadingScreen()
                }

                is UiEvent.Success -> {
                    // Wrap dropdowns into their own composable to avoid re-rendering the entire screen.
                    DropDownSelectionList(
                        title = "Project",
                        dropDownList = projectsList,
                        onItemClick = { println(it) },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 8.dp)
                    )
                    DropDownSelectionList(
                        title = "Task",
                        dropDownList = tasksList,
                        onItemClick = { println(it) },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 8.dp)
                    )
                }
            }

            TimerSessionSection(
                trackerTimer = trackerTimer,
                trackerViewModel = trackerViewModel,
                keyCount = keyCount,
                mouseCount = mouseCount,
                idleTime = totalIdleTime,
                isTrackerRunning = isTrackerRunning,
                sessionManager = sessionManager
            )

            ScreenShotSection(
                lastImageTakenTime = secondsToTime(screenShotTakenTime),
                imageBitmap = screenShotState
            )
            Box {
                Row {
                    TextField(
                        value = customeTimeForIdleTime.toString(),
                        onValueChange = {
                            if (it.isNotEmpty()) {
                                trackerViewModel.addCustomTimeForIdleTime(it.toInt())
                            } else {
                                trackerViewModel.addCustomTimeForIdleTime(10)
                            }

                        },
                        label = { Text("Custom Time") },
                    )
                }
            }

            if (showIdleDialog) {
                CustomAlertDialog(
                    title = "Idle time",
                    text = "You are idle for ${secondsToTime(idleTime)}. Do you want to add idle time to the session?",
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showIdleDialog = false
                                totalIdleTime += idleTime
                                trackerViewModel.resetIdleTimer()
                                trackerViewModel.resumeTracker()
                            }
                        ) {
                            Text("Okay")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showIdleDialog = false
                                trackerViewModel.resetIdleTimer()
                                trackerViewModel.resumeTracker()
                            }
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
                            trackerViewModel.stopTimer()
                            navController.popBackStack()
                        }
                    ) {
                        Text("Okay")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isExitClicked = false
                        }
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
    dropDownList: List<DisplayItem>,
    onItemClick: (String) -> Unit
) {
    var enteredText by remember { mutableStateOf("") }
    var isMenuExpanded by remember { mutableStateOf(false) }
    // Memoize filtered list so that filtering isn’t re-computed on every recompose
    val filteredList by remember(enteredText, dropDownList) {
        derivedStateOf {
            dropDownList.filter {
                when (it) {
                    is Project -> it.projectName.contains(enteredText, ignoreCase = true)
                    is TaskData -> it.taskName.contains(enteredText, ignoreCase = true)
                    else -> true
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            fontSize = 10.sp
        )
        TextField(
            value = enteredText,
            onValueChange = {
                enteredText = it
                isMenuExpanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                    Icon(
                        painter = painterResource(
                            if (isMenuExpanded) Res.drawable.ic_drop_up else Res.drawable.ic_drop_down
                        ),
                        contentDescription = "Toggle Dropdown"
                    )
                }
            }
        )
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            modifier = Modifier.fillMaxWidth(0.8f),
            properties = PopupProperties(focusable = false)
        ) {
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
                                enteredText = item.projectName
                                onItemClick(item.projectId)
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
                                enteredText = item.taskName
                                onItemClick(item.taskId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimerSessionSection(
    modifier: Modifier = Modifier,
    taskName: String = "task",
    trackerTimer: Int,
    trackerViewModel: TrackerViewModel,
    idleTime: Int,
    mouseCount: Int,
    keyCount: Int,
    isTrackerRunning: Boolean,
    sessionManager: SessionManager
) {
    // Use local state to control play/pause. Consider moving this state to the view model if needed.
    var isPlaying by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
            IconButton(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        if (isTrackerRunning) {
                            trackerViewModel.resumeTracker()
                        } else {
                            trackerViewModel.startTimer()
                        }
                    } else {
                        if (isTrackerRunning) {
                            trackerViewModel.stopTimer()
                        } else {
                            trackerViewModel.resetTimer()
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) Res.drawable.ic_pause_circle else Res.drawable.ic_play_arrow
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
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
            Text(
                text = secondsToTime(trackerTimer),
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
                text = "Idle Time",
                color = Color.Red,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = secondsToTime(idleTime),
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
        }
    }
}

@Composable
fun ScreenShotSection(
    modifier: Modifier = Modifier, lastImageTakenTime: String = "10min ago",
    imageBitmap: ImageBitmap? = imageResource(Res.drawable.screen)
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.padding(top = 16.dp),
                bitmap = bitmap,
                contentDescription = "Screenshot"
            )
        }
    }
}


@Composable
fun LogoutDropDown(modifier: Modifier = Modifier, onLogOutClick: () -> Unit) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { isMenuExpanded = false },
        modifier = Modifier.fillMaxWidth(0.8f),
        properties = PopupProperties(focusable = false)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = " TODO()",
                )
            },
            text = {
                Text(
                    text = "Log Out",
                    modifier = Modifier.fillMaxWidth()
                )
            },
            onClick = {
                isMenuExpanded = false
                onLogOutClick()
            }
        )
    }
}