package com.example.agileandroidalpha.feature_board.presentation.edit_subtask

import android.widget.Toast
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FilledIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.Status
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.AddEditTaskEvent
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.EditTaskState
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.ui.theme.Aquamarine
import com.example.agileandroidalpha.ui.theme.Gunmetal
import com.example.agileandroidalpha.ui.theme.IceCold
import com.example.agileandroidalpha.ui.theme.MidnightBlue
import com.example.agileandroidalpha.ui.theme.NightBlue
import com.example.agileandroidalpha.ui.theme.Pool
import com.example.agileandroidalpha.ui.theme.Quartz
import com.example.agileandroidalpha.ui.theme.SoftLilac
import com.example.agileandroidalpha.ui.theme.Surf
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun EditSubTaskScreen(
    navController: NavController,
    state: EditTaskState,
    me: FireUser,
    sid: Long,
    tid: Long,
    id: Long,
    color: Int,
    onEvent: (AddEditTaskEvent) -> Unit = {}
) {
//    val state = viewModel.state.collectAsStateWithLifecycle().value
//    val titleState = viewModel.subTitle.value
//    val bodyState = viewModel.subBody.value
//    val pointsState = viewModel.subPoints.value
//    val priState = viewModel.subPri.value
//    val assState = viewModel.subAss.value
//    val repState = viewModel.subRep.value
//    val subState = viewModel.subStates.value
//    val statState = viewModel.subStatus.value
//    val doneState = viewModel.subDone.value
//    val sprintsList = viewModel.availSprints
//    val userList = viewModel.availUsers
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val randomColor = Subtask.colors.random().toArgb()
    val scaffoldState = rememberScaffoldState()

    val taskBgAnim= remember {
        Animatable(
            Color(if(color != -1) color else randomColor)
        )
    }

    LaunchedEffect(key1 = state.updateFlag) {
        if (state.updateFlag) {
            state.updateFlag = false
        }
    }

    LaunchedEffect(key1 = true) {
//        viewModel.eventFlow.collectLatest { event ->
//            when(event) {
//                is UIEvent.ToastMsg -> {
//                    scaffoldState.snackbarHostState.showSnackbar(
//                        message = event.msg
//                    )
//                }
//                is UIEvent.SaveTask -> {
//                    navController.navigateUp()
//                }
//                is UIEvent.AddSubTask -> {
//                    scaffoldState.snackbarHostState.showSnackbar(
//                        message = "Added subtask",
//                        duration = SnackbarDuration.Short
//                    )
//                }
//                else -> {
//
//                }
//            }
//        }
    }

    Scaffold(
        topBar = {
            com.example.agileandroidalpha.core.TopAppBar(
                title = "Sub-task Editor",
                icon = Icons.Filled.Task,
                onIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerContent = {
            DroidDrawer(
                //currentScreen = Screen.TasksScreen.route,
                navController = navController,
                drawers = state.stories.map{s -> s.title},
                drawerDetails = state.stories.map{s -> s.id!!}
                ,
                closeDrawerAction = {
                    // here - Drawer close
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                clickDrawerSpecial = {
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                header = "Move Sub-task to Other Story"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick =
                {
                    scope.launch {
                            // save it in Firestore and Room
                        Toast.makeText(
                            context,
                            "Saving Subtask #$id of task #$tid of sprint #$sid.",
                            Toast.LENGTH_LONG
                        ).show()
                        onEvent(AddEditTaskEvent.SaveSubTask(
                            id,
                            tid,
                            sid
                        ))
                        Toast.makeText(
                            context,
                            "Successfully saved Subtask #$id of task #$tid of sprint #$sid.",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigateUp()
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.SaveAlt,
                    contentDescription = "Save SubTask",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        },
        scaffoldState = scaffoldState
    ) { padding -> 16.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(taskBgAnim.value)
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Subtask.colors.forEach { color ->
                    val colInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .shadow(4.dp, CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (randomColor == colInt) {
                                    Color.Black
                                } else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                scope.launch {
                                    taskBgAnim.animateTo(
                                        targetValue = Color(colInt),
                                        animationSpec = tween(
                                            durationMillis = 750
                                        )
                                    )
                                    onEvent(AddEditTaskEvent.ChangeColor(colInt))
                                }

                            }
                    )
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(16.dp))   // Change Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.End
            ) {
                var expanded by remember { mutableStateOf(false) }
                val items = enumValues<Status>()
                var selectedIndex by remember { mutableStateOf(0) }
                var textSize by remember { mutableStateOf(Size.Zero)}
                val icon = if (expanded) Icons.Filled.ArrowDropDownCircle
                else Icons.Filled.ArrowDropDown
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .wrapContentSize(Alignment.TopEnd),
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ){
                    TextField(
                        readOnly = true,
                        value = state.status,
                        label = { Text("Status") },
                        trailingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Task Status Dropdown",
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        },
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textSize = coordinates.size.toSize()
                            }
                            .clickable(onClick = { expanded = true })
                            .background(
                                Surf
                            ))
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest =
                        {
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Aquamarine
                            )
                    ) {
                        items.forEachIndexed { index, stat ->
                            DropdownMenuItem(onClick = {
                                selectedIndex = index
                                expanded = false
                                onEvent(AddEditTaskEvent.ChangeStatus(stat.name))
                                //state.done = checkDone(stat.string)
                            }) {
                                Text(text = items[index].string)
                            }
                        }
                    }
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(11.dp))
            TextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.875f),
                label = { Text(
                    text = "Subtask Title",
                    color = MidnightBlue
                ) },
                placeholder = {
                    Text(
                        text = "Enter a title for your subtask",
                        color = NightBlue
                    ) }
                ,
                value = state.title,
                onValueChange = {
                    onEvent(AddEditTaskEvent.EnteredTitle(it))
                },
                maxLines = 3,
                textStyle = MaterialTheme.typography.subtitle2,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )
            Divider()
            Spacer(modifier = Modifier.height(11.dp))
            TextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.9375f),
                label = {
                    Text(
                        text = "Subtask Description",
                        color = MidnightBlue,
                    ) },
                placeholder = {
                    Text(
                        text = "Enter a subtask description . . . ",
                        color = NightBlue
                    ) },
                value = state.desc,
                onValueChange = {
                    onEvent(AddEditTaskEvent.EnteredDesc(it))
                },
                maxLines = 7,
                textStyle = MaterialTheme.typography.caption,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.9375f),
                label = {
                    Text(
                        text = "Body text",
                        color = MidnightBlue,
                    ) },
                placeholder = {
                    Text(
                        text = "Enter subtask body text . . .  ",
                        color = NightBlue
                    ) },
                value = state.body,
                onValueChange = {
                    onEvent(AddEditTaskEvent.EnteredBody(it))
                },
                maxLines = 7,
                textStyle = MaterialTheme.typography.caption,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                colors =  TextFieldDefaults.textFieldColors(
                    textColor = Gunmetal
                )
            )
            Divider()
            Spacer(modifier = Modifier.height(11.dp))

            var expanded by remember { mutableStateOf(false) }
            val items = enumValues<Priority>()//.joinToString(",").split(",")
            var selectedIndex by remember { mutableStateOf(0) }
            Row(modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(all = 50.dp)
            ) {
                Box(modifier = Modifier
                    .wrapContentSize(Alignment.Center)){
                    Text("Priority: ${state.priority}",modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .clickable(onClick = { expanded = true })
                        .background(
                            SoftLilac
                        ))
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                IceCold
                            )
                    ) {
                        items.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                selectedIndex = index
                                expanded = false
                                onEvent(AddEditTaskEvent.ChangePri(items[index].name))
                                //viewModel.onEvent(AddEditTaskEvent.ChangePri(s))
                            }) {
                                val disabledText = if (!expanded) {
                                    " (Disabled)"
                                } else {
                                    ""
                                }
                                Text(text = s.string + disabledText)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(25.dp))

                var expanded2 by remember { mutableStateOf(false) }
                val items2 = longArrayOf(0, 1, 2, 3, 5, 8, 13, 20, 33, 50, 70, 99)
                var selectedIndex2 by remember { mutableStateOf(0) }
                Box(modifier = Modifier
                    .wrapContentSize(Alignment.Center)){
                    Text("Story Points: ${state.points}",modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .clickable(onClick = { expanded2 = true })
                        .background(
                            Quartz
                        ))
                    DropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Aquamarine
                            )
                    ) {
                        items2.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                selectedIndex2 = index
                                expanded2 = false
                                onEvent(AddEditTaskEvent.ChangePoints(items2[index]))
                            }) {
                                val disabledText = if (!expanded2) {
                                    " (Disabled)"
                                } else {
                                    ""
                                }
                                Text(text = "$s" + disabledText)
                            }
                        }
                    }
                }
            }
            Divider()

            var expanded3 by remember { mutableStateOf(false) }
            val items3 = state.users
            var selectedIndex3 by remember { mutableStateOf(0) }
            var textSize by remember { mutableStateOf(Size.Zero)}
            val icon = if (expanded3) Icons.Filled.ArrowDropDownCircle
                        else Icons.Filled.ArrowDropDown
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterHorizontally)
                    .wrapContentSize(Alignment.Center),
                expanded = expanded3,
                onExpandedChange = {
                    expanded3 = !expanded3
                }
            ){
                OutlinedTextField(
                    readOnly = true,
                    value = state.assignee,
                    label = { Text("Assignee") },
                    trailingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = "dropdown menu icon",
                            modifier = Modifier.clickable { expanded3 = !expanded3 }
                        )
                    },
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textSize = coordinates.size.toSize()
                        }
                        .clickable(onClick = { expanded3 = true })
                        .background(
                            Surf
                        ))
                ExposedDropdownMenu(
                    expanded = expanded3,
                    onDismissRequest =
                    {
                        expanded3 = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Aquamarine
                        )
                ) {
                    state.users.forEachIndexed { index, usr ->
                        DropdownMenuItem(
                            enabled = !usr.isDisabled && ((usr.isAdmin || me.isAdmin || me.uid == usr.uid) ||
                                    (me.id == state.creatorID || usr.isVerified && usr.isPowerUser)),
                            onClick = {
                            selectedIndex3 = index
                            expanded3 = false
                            onEvent(AddEditTaskEvent.ChangeAssignee(
                                usr.uid!!,
                                usr.id!!,
                                usr.photo.orEmpty(),
                                usr.name?: usr.email?: "Unnamed Assignee #${usr.id}",
                                usr
                            ))
                            //viewModel.onEvent(AddEditTaskEvent.ChangeAssignee(s.uid!!, s.userId!!, s.name?: s.email?: "Unnamed Assignee #${s.id}"))
                        }) {
                            Text(text = items3[index].name ?: items3[index].email ?: "Unnamed Assignee #${usr.id}")
                        }
                    }
                }
            }
            Divider()
            var expanded4 by remember { mutableStateOf(false) }
            var selectedIndex4 by remember { mutableStateOf(0) }
            var textSize2 by remember { mutableStateOf(Size.Zero)}
            val icon2 = if (expanded4) Icons.Filled.ArrowDropDownCircle
            else Icons.Filled.ArrowDropDown
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterHorizontally)
                    .wrapContentSize(Alignment.Center),
                expanded = expanded4,
                onExpandedChange = {
                    expanded4 = !expanded4
                }
            ){
                OutlinedTextField(
                    readOnly = true,
                    value = state.reporter,
                    label = { Text("Reporter") },
                    trailingIcon = {
                        Icon(
                            imageVector = icon2,
                            contentDescription = "dropdown menu icon",
                            modifier = Modifier.clickable { expanded4 = !expanded4 }
                        )
                    },
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textSize2 = coordinates.size.toSize()
                        }
                        .clickable(onClick = { expanded4 = true })
                        .background(
                            Pool
                        ))
                ExposedDropdownMenu(
                    expanded = expanded4,
                    onDismissRequest =
                    {
                        expanded4 = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Aquamarine
                        )
                ) {
                    state.users.forEachIndexed { index, usr ->
                        DropdownMenuItem(
                            enabled = !usr.isDisabled && ((usr.isAdmin || me.isAdmin || me.uid == usr.uid) ||
                                    (me.id == state.creatorID || usr.isVerified && usr.isPowerUser)),
                            onClick = {
                            selectedIndex4 = index
                            expanded4 = false
                            onEvent(AddEditTaskEvent.ChangeReporter(
                                usr.uid!!,
                                usr.id!!,
                                usr.photo.orEmpty(),
                                usr.name?: "Unnamed User #${usr.id}",
                                usr
                            ))
                        }) {
                            val disabledText = if (!expanded4) {
                                " (Disabled)"
                            } else {
                                ""
                            }
                            Text(text = items3[index].name ?: items3[index].email
                                ?: ("Unnamed User #${usr.id}" + disabledText))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.resolution,
                onValueChange = {
                    onEvent(AddEditTaskEvent.ChangeResolution(it))
                },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            FilledIconButton(
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp, 0.dp, padding.calculateBottomPadding()))
                    .wrapContentSize(Alignment.BottomStart)
                    .onGloballyPositioned { },
                onClick = { scope.launch {
                    navController.navigateUp()
                }},
            ) {
                Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel")
            }

//            if (subState.subtasks != null) {
//                LazyColumn(modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp)
//                ) {items(subState.subtasks.size) { s ->
//                    Box(modifier = Modifier
//                    ) {
//                        var offsetX by remember { mutableStateOf(0f) }
//                        var offsetY by remember { mutableStateOf(0f) }
//
//                        Box(modifier = Modifier
//                            .padding(16.dp)
//                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
//                            .background(Color.Cyan)
//                            .pointerInput(Unit) {
//                                detectDragGestures { change, dragAmount ->
//                                    change.consume()
//                                    offsetX += dragAmount.x
//                                    offsetY += dragAmount.y
//                                }
//                            }
//
//                        ) {
//                            Column(
//
//                            ) {
//
//                                Text(
//                                    text = subState.subtasks[s].title,
//                                    style = MaterialTheme.typography.body1,
//                                    overflow = TextOverflow.Ellipsis,
//                                    modifier = Modifier
//                                )
//                                Text(
//                                    text = subState.subtasks[s].content,
//                                    style = MaterialTheme.typography.body2,
//                                    overflow = TextOverflow.Ellipsis,
//                                    modifier = Modifier
//                                )
//
//                            }
//                        }
//                    }
//                }}
//                Divider()
//                Spacer(modifier = Modifier.height(15.dp))
//            }
        }
    }
}

//FloatingActionButton(
//                onClick = { scope.launch {
//                    viewModel.onEvent(
//                        AddEditTaskEvent.AddSubTask(
//                            subState,
//                            Subtask(
//                                title = "New Subtask - ${subState.sub_subtask?.title}",
//                                content = "New Subtask Body",
//                                color = taskColor,
//                                parentId = id
//                            )
//                        )
//                    )
////                    subState.add(Subtask(
////                        title = "New Subtask",
////                        color = taskColor,
////                        parentId = id
////                    ))
//                }
//                },
//                backgroundColor = MaterialTheme.colors.primary,
//                modifier = Modifier
//                    .wrapContentSize(Alignment.BottomStart)
//            ) {
//                Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel")
//            }

fun checkDone(stat: String): Boolean {
    return stat in listOf("Skipped", "Workaround Available",
        "Done", "Approved", "Closed", "Archived")
}

@ExperimentalMaterialApi
@Preview
@Composable
fun EditSubPreview() {
    EditSubTaskScreen(navController = NavController(context = LocalContext.current),
        state = EditTaskState(), me = FireUser(), sid = 0, tid = 0, id = 0, color = 0)
}