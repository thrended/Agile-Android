package com.example.agileandroidalpha.feature_board.presentation.add_edit_task

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.Status
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.feature_board.domain.model.IssueType
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.components.LabeledHintTextField
import com.example.agileandroidalpha.ui.theme.Aquamarine
import com.example.agileandroidalpha.ui.theme.IceCold
import com.example.agileandroidalpha.ui.theme.Pool
import com.example.agileandroidalpha.ui.theme.Quartz
import com.example.agileandroidalpha.ui.theme.SoftLilac
import com.example.agileandroidalpha.ui.theme.Surf
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun AddEditTaskScreen(
    navController: NavController,
    //state: EditTaskState,
    sid: Long,
    id: Long,
    taskColor: Int,
    viewModel: AddEditTaskViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val titleState = viewModel.taskTitle.value
    val descState = viewModel.taskDesc.value
    val bodyState = viewModel.taskBody.value
    val dodState = viewModel.taskDoD.value
    val pointsState = viewModel.taskPoints.value
    val priState = viewModel.taskPri.value
    val assState = viewModel.taskAss.value
    val repState = viewModel.taskRep.value
    val subState = viewModel.subStates.value
    val statState = viewModel.taskStatus.value
    val logoState = viewModel.taskLogo.value
    val typeState = viewModel.taskType.value
    val resState = viewModel.taskRes.value
    val doneState = viewModel.taskDone.value
    val sprintsList = viewModel.availSprints
    val focusManager = LocalFocusManager.current
    val userList = viewModel.users
    val me = viewModel.me.asStateFlow()

    val scaffoldState = rememberScaffoldState()

    val taskBgAnim= remember {
        Animatable(
            Color(if(taskColor != -1) taskColor else viewModel.taskColor.value)
        )
    }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is AddEditTaskViewModel.UIEvent.ShowSNB -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.msg
                    )
                }
                is AddEditTaskViewModel.UIEvent.SaveTask -> {
                    navController.navigateUp()
                }
                is AddEditTaskViewModel.UIEvent.AddSubtask -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Added subtask",
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {

                }
            }
        }
    }

    Scaffold(
        topBar = {
            com.example.agileandroidalpha.core.TopAppBar(
                title = "Story/Task Editor",
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
                drawers = sprintsList.map{s -> s.title},
                drawerDetails = sprintsList.map{s -> s.sprintId!!}
                ,
                closeDrawerAction = {
                    // here - Drawer close
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                clickDrawerSpecial = {
                    scope.launch {
                        viewModel.onEvent(AddEditTaskEvent.ChangeSprint(it))
                        scaffoldState.drawerState.close()
                    }
                },
                header = "Assign Story to Sprint"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick =
                {
                    viewModel.onEvent(AddEditTaskEvent.SaveTask(sid, id))
                },
                backgroundColor = MaterialTheme.colors.primary,
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save Task")
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
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Task.colors.forEach { color ->
                    val colInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .shadow(5.dp, CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (viewModel.taskColor.value == colInt) {
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
                                }
                                viewModel.onEvent(AddEditTaskEvent.ChangeColor(colInt))
                            }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Task.subColors.forEach { color ->
                    val colInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .shadow(4.dp, CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (viewModel.taskColor.value == colInt) {
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
                                }
                                viewModel.onEvent(AddEditTaskEvent.ChangeColor(colInt))
                            }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Task.moreColors.forEach { color ->
                    val colInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .shadow(3.dp, CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (viewModel.taskColor.value == colInt) {
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
                                }
                                viewModel.onEvent(AddEditTaskEvent.ChangeColor(colInt))
                            }
                    )
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(8.dp))   // Change Issue Type
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                var ex by remember { mutableStateOf(false) }
                val itm = enumValues<IssueType>()//.map{ s -> s.string }
                var idx by remember { mutableStateOf(0) }
                var sz by remember { mutableStateOf(Size.Zero)}
                val icn = if (ex) Icons.Filled.ArrowDropDownCircle
                else Icons.Filled.ArrowDropDown
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        //.wrapContentSize(Alignment.TopStart),
                        .fillMaxWidth(0.4f),
                    expanded = ex,
                    onExpandedChange = {
                        ex = !ex
                    }
                ){
                    OutlinedTextField(
                        readOnly = true,
                        value = typeState,
                        label = { Text("Issue Type") },
                        trailingIcon = {
                            Icon(
                                imageVector = icn,
                                contentDescription = "Task Type Dropdown",
                                modifier = Modifier.clickable { ex = !ex }
                            )
                        },
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                sz = coordinates.size.toSize()
                            }
                            .clickable(onClick = { ex = true })
                            .background(
                                Surf
                            ))
                    ExposedDropdownMenu(
                        expanded = ex,
                        onDismissRequest =
                        {
                            ex = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Aquamarine
                            )
                    ) {
                        itm.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                idx = index
                                ex = false
                                viewModel.onEvent(AddEditTaskEvent.ChangeType(s.name))
                            }) {
                                Text(text = itm[index].string)
                            }
                        }
                    }
                }
                var expanded by remember { mutableStateOf(false) }
                val items = enumValues<Status>()//.map{ s -> s.string }
                var selectedIndex by remember { mutableStateOf(0) }
                var textSize by remember { mutableStateOf(Size.Zero)}
                val icon = if (expanded) Icons.Filled.ArrowDropDownCircle
                else Icons.Filled.ArrowDropDown
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        //.wrapContentSize(Alignment.TopEnd)
                        .fillMaxWidth(0.6f),
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ){
                    OutlinedTextField(
                        readOnly = true,
                        value = statState,
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
                        items.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                selectedIndex = index
                                expanded = false
                                viewModel.onEvent(AddEditTaskEvent.ChangeStatus(s.name))
                            }) {
                                Text(text = items[index].string)
                            }
                        }
                    }
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(3.dp))
            LabeledHintTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.875f),
                text = titleState.text,
                hint = titleState.hint,
                label = "Title",
                onValueChange = {
                    viewModel.onEvent(AddEditTaskEvent.EnteredTitle(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditTaskEvent.ChangeTitleFocus(it))
                },
                showHint = titleState.isHintVisible,
                maxLines = 3,
                textStyle = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(3.dp))
            Divider()
            Spacer(modifier = Modifier.height(3.dp))
            LabeledHintTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.925f),
                text = descState.text,
                hint = descState.hint,
                label = "Description",
                onValueChange = {
                    viewModel.onEvent(AddEditTaskEvent.EnteredDesc(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditTaskEvent.ChangeDescFocus(it))
                },
                showHint = descState.isHintVisible,
                maxLines = 3,
                textStyle = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(3.dp))
            Divider()
            Spacer(modifier = Modifier.height(3.dp))
            LabeledHintTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.15f),
                text = bodyState.text,
                hint = bodyState.hint,
                label = "Body Text",
                onValueChange = {
                    viewModel.onEvent(AddEditTaskEvent.EnteredBody(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditTaskEvent.ChangeBodyFocus(it))
                },
                showHint = bodyState.isHintVisible,
                textStyle = MaterialTheme.typography.caption
            )
            Spacer(modifier = Modifier.height(3.dp))
            Divider()
            Spacer(modifier = Modifier.height(3.dp))
            LabeledHintTextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.925f),
                text = dodState.text,
                hint = dodState.hint,
                label = "Definition of Done",
                onValueChange = {
                    viewModel.onEvent(AddEditTaskEvent.EnteredDoD(it))
                },
                onFocusChange = {
                    viewModel.onEvent(AddEditTaskEvent.ChangeDoDFocus(it))
                },
                showHint = dodState.isHintVisible,
                maxLines = 3,
                textStyle = MaterialTheme.typography.caption
            )
            Divider()
            Spacer(modifier = Modifier.height(6.dp))                     // End of Text Boxes

            var expanded5 by remember { mutableStateOf(false) }
            val items5 = listOf("Unresolvable", "Unresolved", "Budget Cut", "Extension Needed", "Client Changed Mind",
                "Cancelled", "Postponed", "Recurring Issues", "User Error", "Awaiting Review",
                "Reviewed", "Awaiting Approval", "Approved", "Patched", "Requires Special Patch",
                "Fixed in Newer Version", "Fixed in Update", "Fixed in New Product", "No longer relevant",
                "Resolved with Workaround", "Resolved")
            var selectedIndex5 by remember { mutableStateOf(0) }
            Row(modifier = Modifier
                //.wrapContentSize(Alignment.Center)
                .fillMaxWidth()
            ){
                Text("Resolution: ${state.resolution}",modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clickable(onClick = { expanded5 = true })
                    .background(
                        Quartz
                    ))
                DropdownMenu(expanded = expanded5, onDismissRequest = { expanded5 = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Aquamarine
                        )
                ) {
                    items5.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            selectedIndex5 = index
                            expanded5 = false
                            viewModel.onEvent(AddEditTaskEvent.ChangeResolution(s))
                        },
                            enabled = index < 12 || state.me?.isAdmin == true
                        ) {
                            val disabledText = if (!expanded5) {
                                " (Disabled)"
                            } else {
                                ""
                            }
                            androidx.compose.material3.Text(text = s + disabledText)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            var expanded by remember { mutableStateOf(false) }      // Change Priority Menu
            val items = enumValues<Priority>()//.map{ p -> p.string }
            var selectedIndex by remember { mutableStateOf(0) }
            Row(modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(all = 5.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .wrapContentSize(Alignment.Center)){
                    Text("Priority: ${priState.text}",modifier = Modifier
                        .fillMaxWidth(0.35f)
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
                                viewModel.onEvent(AddEditTaskEvent.ChangePri(s.name))
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
//            Row(
//
//            )
//            {
                var expanded2 by remember { mutableStateOf(false) }
                val items2 = longArrayOf(0, 1, 2, 3, 5, 8, 13, 20, 33, 50, 99, 9999)
                var selectedIndex2 by remember { mutableStateOf(0) }
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = "Story Points: $pointsState",
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .clickable(onClick = { expanded2 = true })
                            .background(Quartz)
                    )
                    DropdownMenu(
                        expanded = expanded2, onDismissRequest = { expanded2 = false },
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
                                viewModel.onEvent(AddEditTaskEvent.ChangePoints(s))
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
//            }

//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth(0.4f)
//                        .background(Quartz),
//                    value = "Resolution: $resState",
//                    placeholder = { Text (text = "Resolution ") },
//                    onValueChange = {
//                        viewModel.onEvent(AddEditTaskEvent.ChangeResolution(it))
//                    },
//                    shape = RoundedCornerShape(15.dp),
//                    keyboardOptions = KeyboardOptions(
//                        imeAction = ImeAction.Done),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            focusManager.clearFocus()
//                        }
//                    )
//                )
            }
            Divider()

            var expanded3 by remember { mutableStateOf(false) }
            val items3 = userList.map { itm -> itm.name ?: itm.email ?: "Unnamed Assignee #${itm.id}" }
            var selectedIndex3 by remember { mutableStateOf(0) }
            var textSize by remember { mutableStateOf(Size.Zero)}
            val icon = if (expanded3) Icons.Filled.ArrowDropDownCircle
            else Icons.Filled.ArrowDropDown
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(0.825f)
                    .align(Alignment.CenterHorizontally)
                    .wrapContentSize(Alignment.Center),
                expanded = expanded3,
                onExpandedChange = {
                    expanded3 = !expanded3
                }
            ){
                OutlinedTextField(
                    readOnly = true,
                    value = assState.text,
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
                    state.users.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            enabled = !s.isDisabled && ((s.isAdmin || me.value.isAdmin || me.value.uid == s.uid) ||
                                    (me.value.uid == s.uid || s.isVerified && s.isPowerUser)),
                            onClick = {
                            selectedIndex3 = index
                            expanded3 = false
                            viewModel.onEvent(AddEditTaskEvent.ChangeAssignee(
                                s.uid!!, s.userId!!, s.photo,s.name?: s.email?: "Unnamed Assignee #${s.id}", s))
                        }) {
                            Text(text = items3[index])
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
                    .fillMaxWidth(0.825f)
                    .align(Alignment.CenterHorizontally)
                    .wrapContentSize(Alignment.Center),
                expanded = expanded4,
                onExpandedChange = {
                    expanded4 = !expanded4
                }
            ){
                OutlinedTextField(
                    readOnly = true,
                    value = repState.text,
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
                    userList.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            enabled = !s.isDisabled && ((s.isAdmin || me.value.isAdmin || me.value.uid == s.uid) ||
                                    (me.value.id == state.creatorID || s.isVerified && s.isPowerUser)),
                            onClick = {
                            selectedIndex4 = index
                            expanded4 = false
                            viewModel.onEvent(AddEditTaskEvent.ChangeReporter(
                                s.uid!!, s.userId!!, s.photo,s.name ?: s.email?: "Unnamed User", s))
                        }) {
                            val disabledText = if (!expanded4) {
                                " (Disabled)"
                            } else {
                                ""
                            }
                            Text(text = items3[index] + " $disabledText")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = "Modified by: ${me.value.name ?: "Anonymous User"}",
                onValueChange = {
                    //viewModel.onEvent(AddEditTaskEvent.ChangeReporter(it))
                },
                shape = RoundedCornerShape(15.dp),
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

            if (subState.subtasks != null) {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ) {items(subState.subtasks.size) { s ->
                    Box(modifier = Modifier
                    ) {
                        var offsetX by remember { mutableStateOf(0f) }
                        var offsetY by remember { mutableStateOf(0f) }

                    Box(modifier = Modifier
                        .padding(16.dp)
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .background(Color.Cyan)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }

                    ) {
                        Column(

                        ) {

                            Text(
                                text = subState.subtasks[s].title,
                                style = MaterialTheme.typography.body1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                            )
                            Text(
                                text = subState.subtasks[s].content,
                                style = MaterialTheme.typography.body2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                            )

                        }
                    }
                    }
                }}
                Divider()
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }

    fun toLogo(typ: String): Int {
        return when (typ) {
            "Task" -> {
                R.drawable.id_viridia
            }
            "Story" -> {
                R.drawable.id_blue
            }
            "Subtask" -> {
                R.drawable.box_green
            }
            "Impediment" -> {
                R.drawable.ic_wall
            }
            "Bug" -> {
                R.drawable.box_red
            }
            "Feature" -> {
                R.drawable.ic_grab
            }
            "Initiative" -> {
                R.drawable.ic_pipe
            }
            "Issue" -> {
                R.drawable.ic_unknown
            }
            "Test" -> {
                R.drawable.ic_signal
            }
            "Spike" -> {
                R.drawable.ic_trap
            }
            "ChangeRequest" -> {
                R.drawable.ic_swap
            }
            "Epic" -> {
                R.drawable.ic_twins
            }
            else -> {
                -1
            }
        }
    }
}

//FloatingActionButton(
//                onClick = { scope.launch {
//                    viewModel.onEvent(
//                        AddEditTaskEvent.AddSubTask(
//                            subState,
//                            Subtask(
//                                title = "New Subtask - ${subState.task_subtask?.title}",
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