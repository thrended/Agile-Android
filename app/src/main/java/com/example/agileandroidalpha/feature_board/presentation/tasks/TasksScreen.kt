package com.example.agileandroidalpha.feature_board.presentation.tasks

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.presentation.sprint.SprintEvent
import com.example.agileandroidalpha.feature_board.presentation.tasks.components.OrderSection
import com.example.agileandroidalpha.feature_board.presentation.tasks.components.TaskItem
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.ui.theme.Aquamarine
import com.example.agileandroidalpha.ui.theme.Cerulean
import com.example.agileandroidalpha.ui.theme.Quartz
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.min

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterial3Api
@Composable
fun TasksScreen(
    navController: NavController,
    //state: TasksState,
    uid: String?,
    sid: Long,
    viewModel: TasksViewModel = hiltViewModel(),
    initAction: () -> Unit = { viewModel.onEvent(SprintEvent.Refresh(1)) },
){
    val context = LocalContext.current
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val currentSprint = viewModel.selectedSprint.value
    val selectedSprint = Pair(viewModel.selectedSprintId.value, viewModel.selectedSprintTitle.value)
    val numSprints = viewModel.numSprints.value
    val started = viewModel.started.value
    val total = viewModel.points.value
    val rem = viewModel.rem.value
    val cd = viewModel.cd.value
    //val selectedSprintId = viewModel.selectedSprintId.value
    //val selectedSprintTitle = viewModel.selectedSprintTitle.value
    //val sheet = androidx.compose.material3.rememberModalBottomSheetState()
    var sheetMode by rememberSaveable{ mutableStateOf(0)}
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val sprints = viewModel.sprintLib.collectAsStateWithLifecycle().value
    LaunchedEffect(key1 = state.flagForUpdate){
        viewModel.reload()
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is TasksViewModel.UIEvent.ShowSNB -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.msg
                    )
                }
                is TasksViewModel.UIEvent.Refresh -> {
                    viewModel.onEvent(SprintEvent.Refresh(event.id))
                }
                is TasksViewModel.UIEvent.Reload -> {
                    viewModel.onEvent(SprintEvent.Reload)
                }
                is TasksViewModel.UIEvent.AddSprint -> {
                    viewModel.onEvent(SprintEvent.Reload)
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "New Sprint Added",
                        duration = SnackbarDuration.Short
                    )
                }
                is TasksViewModel.UIEvent.ChangeSprint -> {
                    viewModel.onEvent(SprintEvent.Refresh(event.id))
                }

                is TasksViewModel.UIEvent.StartPause -> {
                    viewModel.onEvent(SprintEvent.Reload)
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = if (event.bool) "Sprint started. Duration of sprint : ${event.days} days"
                                else "Sprint paused with ${event.days} days remaining."
                    )
                }
                is TasksViewModel.UIEvent.AddSubtask -> {
                    viewModel.onEvent(SprintEvent.Refresh(selectedSprint.first))
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "New Subtask Added",
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {

                }
            }
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            when(sheetMode) {
                0 -> {  // Sprint Selection
                    if(state.sprintList.isNotEmpty() || state.sprints.isNotEmpty()) {
                        Box(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.TopCenter
                        ){
                            Text (
                                text = "Select a sprint to clone it."
                            )
                        }
                    LazyColumn(
                        content = {
                            itemsIndexed(state.sprints) { i, sp ->
                                val k = sp.id ?: 1L
                                val cloneText = if (sp.cloned) {
                                    " [CLONE] of "
                                } else {
                                    ""
                                }
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            scope.launch {
                                                viewModel.onEvent(
                                                    SprintEvent.CloneSprint(
                                                        sp,
                                                        state.allTasks
                                                    )
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Sprint $k has successfully been cloned.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                       },
                                    text = { Text("Sprint $k - $cloneText ${sp.title}") }
                                )
                            }
                        }
                    ) }
                    else {
                        Text("No avail sprints.")
                    }
                }
                1 -> {  // Sprint Editor
                    sprints.forEach { s ->  }
                }
                2 -> {  // Subtask Editor

                }
                else -> {}
            }
        },
        modifier = Modifier,
        sheetState = sheetState
    )
    {

    Scaffold(
        topBar = {
            com.example.agileandroidalpha.core.TopAppBar(
                title = "Manage Sprints",
                icon = Icons.Filled.Task,
                onIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                        viewModel.onEvent(SprintEvent.Refresh(selectedSprint.first))
                    }
                },
                icon2 = if(state.sprintStarted) {
                            if( state.timeRem > 13 ) Icons.Filled.HourglassTop
                            else if (state.timeRem > 6) Icons.Filled.HourglassBottom
                            else Icons.Filled.HourglassEmpty
                }
                            else if(state.sprintActive && !state.sprintPaused) Icons.Filled.HourglassFull
                            else Icons.Filled.HourglassDisabled,
                onIcon2Click = {
                    scope.launch {
                        state.currentSprint?.let {
                            viewModel.onEvent(
                                SprintEvent.StartPause(
                                    it,
                                    !it.active,
                                    it.info.countdown
                                )
                            )
                        }
                    }
                },
                icon3 = Icons.Filled.CloudSync,//Icons.Filled.CallToAction,
                onIcon3Click =
                {
                    sheetMode = 1
                    scope.launch { viewModel.syncFromCloud { _ ->
                        Toast.makeText(
                            context,
                            "Successfully downloaded Cloud Firestore data to device.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } }
                },
                icon4 = Icons.Filled.ControlPointDuplicate,
                onIcon4Click =
                {
                    sheetMode = 0
                    scope.launch { sheetState.show() }
                },
                title2 = "${total - rem} / $total Points Completed",
                title3 = if(state.sprintStarted) "${state.timeRem} Days Remaining" else "Duration : ${state.timeRem} Days"
            )

        },
        drawerContent = {
            DroidDrawer(
                //currentScreen = Screen.TasksScreen.route,
                navController = navController,
                closeDrawerAction = {
                    // here - Drawer close
                    scope.launch {
                        scaffoldState.drawerState.close()
                        viewModel.onEvent(SprintEvent.Refresh(selectedSprint.first))
                    }
                },
                clickDrawerAction = {
                    scope.launch {
                        scaffoldState.drawerState.close()
                        navController.navigate(it)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddEditTaskScreen.route +
                            "?sid=${selectedSprint.first}")
                },
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .displayCutoutPadding()
            ) {
                Icon(imageVector = Icons.Default.AddTask, contentDescription = "Add Task")
            }
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                onBarClicked = {
                    navController.navigate(it.route)
                },
            )
        },
        scaffoldState = scaffoldState,
    ) { padding -> 16.dp
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.onEvent(SprintEvent.QuickAdd)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.DomainAdd,
                        contentDescription = "Add Sprint"
                    )
                }
                //OutlinedCard(onClick = { /*viewModel.onEvent(TasksEvent.ChangeSprint(id))*/ })
                //{
                var savedText by remember { mutableStateOf(state.sprintTitle) }
                var canEdit by remember { mutableStateOf(false) }
                var expanded by remember { mutableStateOf(false) }
                var selectedIndex by remember { mutableStateOf(0) }
                var textSize by remember { mutableStateOf(Size.Zero)}
                val iconLead = if (canEdit) Icons.Filled.EditAttributes else Icons.Filled.Lock
                val iconTrail = if (state.started) Icons.Filled.Start else Icons.Filled.Pause
                //val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentSize(Alignment.TopCenter),
                    expanded = expanded,
                    onExpandedChange = {
                        scope.launch {
                            viewModel.onEvent(SprintEvent.Reload)
                        }
                        expanded = !expanded
                    }
                ){
                    val cloneText = if (currentSprint.cloned) {
                        " [CLONE] "
                    } else {
                        ""
                    }
                    TextField(
                        enabled = state.sprintCount > 0,
                        readOnly = !canEdit,
                        value = state.sprintTitle + cloneText, //if(state.currentSprint?.info?.cloned == true) "${state.sprintTitle} [CLONE]" else state.sprintTitle
                        leadingIcon = {
                            Icon(
                                imageVector = iconLead,
                                contentDescription = "dropdown menu icon",
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(SprintEvent.Reload)
                                    canEdit = !canEdit
                                }
                            )
                        },
                        label = { Text("Change Sprint") },
                        trailingIcon = {
                            Icon(
                                imageVector = iconTrail,
                                contentDescription = "Start/Pause",
                                modifier = Modifier.clickable(
                                    enabled = canEdit
                                ) {
                                    scope.launch {
                                        state.selectedSprint?.let {
                                            viewModel.onEvent(
                                                SprintEvent.StartPauseTEST(
                                                    it,
                                                    it.active == false,
                                                    it.countdown
                                                )
                                            )
                                        }
                                    }
                                    canEdit = !canEdit
                                }
                            )
                        },
                        textStyle = MaterialTheme.typography.h5,
                        onValueChange = { s -> state.currentSprint?.let {viewModel.onEvent(SprintEvent.RenameSprint(it, s)) } },
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textSize = coordinates.size.toSize()
                            }
                            .clickable(onClick = { expanded = true })
                            .wrapContentSize(Alignment.TopCenter)
                            .background(
                                color = Quartz,
                                shape = RoundedCornerShape(20.dp)
                            )
//                            .clickable {
//                                scope.launch {
//                                    if (sheetState.isVisible) sheetState.hide()
//                                    else sheetState.show()
//                                }
//                            }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest =
                        {
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(
                                Aquamarine
                            )
                            .alignByBaseline()
                            .wrapContentSize(Alignment.TopCenter),
                    ) {
                        state.sprints.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                selectedIndex = index
                                expanded = false
                                viewModel.onEvent(TasksEvent.ChangeSprint(s))
                            }) {
                                val cloneText = if (s.cloned) {
                                    " [CLONE] "
                                } else {
                                    ""
                                }
                                Text(text = cloneText + s.title?.let { s.title!!.slice(0..min(s.title!!.length - 1, 15)) }
                                + "- ${s.id}")
                            }

                        }
//                        state.sprintList.forEachIndexed { index, s ->
//                            DropdownMenuItem(onClick = {
//                                selectedIndex = index
//                                expanded = false
//                                viewModel.onEvent(TasksEvent.ChangeSprint(s))
//                            }) {
//                                val cloneText = if (s.info.cloned) {
//                                    " [CLONE] "
//                                } else {
//                                    ""
//                                }
//                                Text(text = cloneText + s.title.slice(0..min(s.title.length - 1, 15))
//                                        + "- ${s.sprintId}")
//                            }
//
//                        }
                    }
                }
//                    Text(
//                        modifier = Modifier
//                            .wrapContentSize(Alignment.TopCenter),
//                        text = "$selectedSprint.second - ${selectedSprint.first}",
//                        style = MaterialTheme.typography.h5
//                    )
                //}
                IconButton(
                    onClick = {
                        viewModel.onEvent(TasksEvent.ToggleOrderSection)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort"
                    )
                }
            }
            AnimatedVisibility(
                visible = state.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    taskOrder = state.taskOrder,
                    onOrderChange = {
                        viewModel.onEvent(TasksEvent.Order(it))
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if(state.stories.isNotEmpty() && state.sprints.isNotEmpty() && state.allTasks.isNotEmpty()) {
            val collapsedState = remember(state.allTasks) { state.allTasks.map { i -> i.task.done }.toMutableStateList() }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(state.allTasks) { i, item ->
                    TaskItem(
                        task = item.task,
                        subtasks = item.subtasks,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Screen.AddEditTaskScreen.route +
                                            "?sid=${item.task.sprintId}&id=${item.task.id}&taskColor=${item.task.color}"
                                )
                            },
                        collapsed = collapsedState[i],
                        onAddSubClick = {
                            viewModel.onEvent(
                                TasksEvent.AddSubTask(
                                    item.task,
                                    Subtask(
                                        title = "Subtask # ${item.subtasks.size + 1}",
                                        color = item.task.color,
                                        id = item.subtasks.size.toLong(),
                                        uid = uid,
                                        parentId = item.task.taskId,
                                        sprintId = item.task.sprintId,
                                        createdBy = uid,
                                    )
                                )
                            )
                        },
                        onCheckboxClick = {
                            scope.launch {
                                viewModel.onEvent(TasksEvent.MarkComplete(
                                    item
                                ))
                            }
                        },
                        onCheckSubtask = { subtask, bool ->
                            scope.launch {
                                viewModel.onEvent(TasksEvent.MarkSubtask(
                                    item,
                                    subtask,
                                    bool
                                ))
                            }
                        },
                        onDeleteClick = {
                            viewModel.onEvent(TasksEvent.DeleteTask(item.task, item.subtasks))
                            scope.launch {
                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Deleted Task",
                                    actionLabel = "Undo"
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onEvent(TasksEvent.RestoreTask)
                                }
                            }
                        },
                        onDeleteSubtask = { subtask ->
                            scope.launch {
                                viewModel.onEvent(TasksEvent.DeleteSubtask(subtask))
                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Deleted Subtask",
                                    actionLabel = "Undo"
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onEvent(TasksEvent.RestoreSubtask)
                                }
                            }
                        },
                        onDragSubtask = { sub, stat, done ->
                            scope.launch {
                                viewModel.onEvent(TasksEvent.DragSubtask(
                                    item,
                                    sub,
                                    stat,
                                    done
                                ))
                            }
                        },
                        onExpandClick = { bool ->
                            collapsedState[i] = bool
                        },
                        onEditSubtask = { sub, id ->
                            navController.navigate(
                                Screen.EditSubTaskScreen.route +
                                "?id=${id}&tid=${sub.parentId}&sid=${sub.sprintId}&color=${sub.color}"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                }
            } else if (state.sprintCount == 0 || state.sprints.isEmpty() || state.sprintList.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 25.dp, start = 25.dp)
                            .fillMaxWidth(0.9f),
                        text = "No sprints. Create your first sprint by clicking on the leftmost Domain Add button.",
                        color = Cerulean,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Justify
                    )

                }
            } else {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 25.dp, start = 25.dp)
                            .fillMaxWidth(0.9f),
                        text = "No Stories. Click the button in the bottom right corner to create a new Story Task.",
                        color = Cerulean,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    } }
}

fun allSubtasksDone(subtasks: List<Subtask>): Boolean {
    subtasks.forEach { sub->
        if (!sub.done)
            return false
    }
    return true
}

//AnimatedVisibility(
//                        visible = expanded,
//                        enter = fadeIn() + slideInVertically(),
//                        exit = fadeOut() + slideOutVertically()
//                    )