@file:OptIn(ExperimentalMaterialApi::class)

package com.example.agileandroidalpha.feature_board.presentation.sprint

import android.widget.Toast
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.CommentsDisabled
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.agileandroidalpha.Status
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.ui.theme.Aquamarine
import com.example.agileandroidalpha.ui.theme.Gunmetal
import com.example.agileandroidalpha.ui.theme.IceCold
import com.example.agileandroidalpha.ui.theme.MidnightBlue
import com.example.agileandroidalpha.ui.theme.MidnightPurple
import com.example.agileandroidalpha.ui.theme.NightBlue
import com.example.agileandroidalpha.ui.theme.Pool
import com.example.agileandroidalpha.ui.theme.SoftLilac
import com.example.agileandroidalpha.ui.theme.Surf
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterialApi
@Composable
fun EditSprintScreen(
    navController: NavController,
    state: SprintState,
    id: Long,
    uid: String,
    color: Int,
    onEvent: (SprintEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    fun msToDays(tym: Long): Long {
        return Duration.convert(tym + 0.0, DurationUnit.MILLISECONDS, DurationUnit.DAYS).roundToLong()
    }

    fun daysToMs(days: Int): Long {
        return Duration.convert(days + 0.0, DurationUnit.DAYS, DurationUnit.MILLISECONDS).roundToLong()
    }

    fun dxBool (start: Long?, end: Long?): Boolean {
        if (start == null || end == null) return true
        val x =  Duration.convert((end - start) + 0.0, DurationUnit.MILLISECONDS, DurationUnit.DAYS).roundToInt()
        return x in 7..70 && start + daysToMs(2) > System.currentTimeMillis()
    }

    fun dx(start: Long?, end: Long?): Int {
        if (start == null || end == null) return 0
        return Duration.convert((end - start) + 0.0, DurationUnit.MILLISECONDS, DurationUnit.DAYS).roundToInt()
    }
    val canComment = state.user?.isVerified == true || state.auth?.isEmailVerified == true
    var dtDialog by remember { mutableStateOf(false) }
    var tymDialog by remember { mutableStateOf(false) }
    var tym2Dialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val randomColor = Subtask.colors.random().toArgb()
    val scaffoldState = rememberScaffoldState()
    val dtPickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = System.currentTimeMillis(),
        initialSelectedEndDateMillis = System.currentTimeMillis() + daysToMs(state.sprint?.duration?: 21),
        yearRange = IntRange(2023, 2025),
        initialDisplayMode = DisplayMode.Picker
    )
    val dtTimePickerState = rememberTimePickerState(
        initialHour = 10,
        initialMinute = 30,
        is24Hour = false
    )
    val dtTimePicker2State = rememberTimePickerState(
        initialHour = 10,
        initialMinute = 30,
        is24Hour = false
    )
    val taskBgAnim= remember {
        Animatable(
            Color(if(color != -1) color else randomColor)
        )
    }
    var pickedStartDate by remember {
        mutableStateOf(LocalDate.now())
    }
    val formattedStartDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("MMM dd yyyy")
                .format(pickedStartDate)
        }
    }
    var pickedEndDate by remember {
        mutableStateOf(LocalDate.now())
    }
    val formattedEndDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("MMM dd yyyy")
                .format(pickedEndDate)
        }
    }
    var pickedMeetingTime by remember {
        mutableStateOf(LocalTime.NOON)
    }
    val formattedMeetingTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("hh:mm")
                .format(pickedMeetingTime)
        }
    }
    var pickedReviewTime by remember {
        mutableStateOf(LocalTime.NOON)
    }
    val formattedReviewTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("hh:mm")
                .format(pickedReviewTime)
        }
    }
    fun formatTime(timestamp: Long) {

        val dtFormatter = DateTimeFormatter
            .ofPattern(
                "dd MM yyyy",
                Locale.getDefault()
            )
            .format(Instant.ofEpochMilli(timestamp))
    }

    LaunchedEffect(key1 = state.updateFlag) {
        if (state.updateFlag) {
            state.updateFlag = false
        }
    }

    Box(

    ) {
        Canvas(modifier = Modifier.matchParentSize())
        {
            val clipPath = Path().apply {
                lineTo(size.width - 50.dp.toPx(), 0f)
                lineTo(size.width, 50.dp.toPx())
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            clipPath(clipPath) {
//                drawImage(
//                    image = ImageBitmap.imageResource(R.drawable.profsad)
//                )
            }
        }

    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = "Sprint Editor",
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
                header = "Sprint Editor Drawer"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick =
                {
                    scope.launch {
                        // save it in Firestore and Room
                        onEvent(
                            SprintEvent.SaveSprint(
                            id,
                        ))
                        Toast.makeText(
                            context,
                            "Saved Sprint #$id created by User $uid.",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigateUp()
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.SaveAlt,
                    contentDescription = "Save Sprint",
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
                                    onEvent(SprintEvent.ChangeColor(colInt))
                                }

                            }
                    )
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(4.dp))   // Change Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                FilledTonalButton(
                    modifier = Modifier,
                    onClick =
                    {
                        dtDialog = !dtDialog
                    }
                ) {
                    Text("Sprint Calendar ⏲")
                }

                var expanded by remember { mutableStateOf(false) }
                val items = enumValues<Status>()
                var selectedIndex by remember { mutableStateOf(0) }
                var textSize by remember { mutableStateOf(Size.Zero) }
                val icon = if (expanded) Icons.Filled.ArrowDropDownCircle
                else Icons.Filled.ArrowDropDown
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxWidth(0.7f)
                        .wrapContentSize(Alignment.TopEnd),
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ){
                    TextField(
                        readOnly = true,
                        value = state.status.orEmpty(),
                        label = { androidx.compose.material.Text("Status") },
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
                                onEvent(SprintEvent.ChangeStatus(stat.name))
                                //state.done = checkDone(stat.string)
                            }) {
                                Text(text = items[index].string)
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceBetween
            ){

//                Text(
//                    text = "Start Date: $formattedStartDate\n" +
//                        "\nEnd Date: $formattedEndDate",
//                    color = MetallicBronze,
//                    fontWeight = Bold
//                )

                var expanded by remember { mutableStateOf(false) }
                val items = floatArrayOf(0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f, 0.4f, 0.45f, 0.5f, 0.55f,
                    0.6f, 0.65f, 0.7f, 0.75f, 0.8f, 0.85f, 0.875f, 0.9f, 0.925f, 0.95f, 0.97f, 0.99f, 1f)
                var selectedIndex by remember { mutableStateOf(0) }
                Box(modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)){
                    androidx.compose.material.Text("Target Goal: ${state.target * 100f}%",modifier = Modifier
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
                                onEvent(SprintEvent.ChangeTarget(items[index]))
                                //viewModel.onEvent(AddEditTaskEvent.ChangePri(s))
                            }) {
                                val disabledText = if (!expanded) {
                                    " (Disabled)"
                                } else {
                                    ""
                                }
                                Text(text = "${state.target * 100f}%" + disabledText)
                            }
                        }
                    }
                }
            }
            Divider()
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.875f),
                label = { androidx.compose.material.Text(
                    text = "Sprint Title",
                    color = MidnightBlue
                ) },
                placeholder = {
                    androidx.compose.material.Text(
                        text = "Enter a title for your sprint",
                        color = NightBlue
                    ) }
                ,
                value = state.title.orEmpty(),
                onValueChange = {
                    onEvent(SprintEvent.ChangeTitle(it))
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
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.9375f),
                label = {
                    androidx.compose.material.Text(
                        text = "Sprint Description",
                        color = MidnightBlue,
                    ) },
                placeholder = {
                    androidx.compose.material.Text(
                        text = "Enter a sprint description . . . ",
                        color = NightBlue
                    ) },
                value = state.desc.orEmpty(),
                onValueChange = {
                    onEvent(SprintEvent.ChangeDesc(it))
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
            Spacer(modifier = Modifier.height(6.dp))
            if (dtDialog)
            {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                        DatePickerDialog(
                            modifier = Modifier
                                .fillMaxSize(),
                            onDismissRequest = { dtDialog = !dtDialog },
                            confirmButton = {
                                FilledTonalButton(
                                    onClick =
                                    {
                                        dtPickerState.selectedStartDateMillis?.let {
                                            state.startDate = it
                                            state.duration = dx(
                                                dtPickerState.selectedStartDateMillis,
                                                dtPickerState.selectedEndDateMillis
                                            )
                                            state.countdown = state.duration - state.elapsed
                                            pickedStartDate = LocalDate.ofEpochDay(msToDays(it))
                                        }
                                        dtPickerState.selectedEndDateMillis?.let {
                                            state.endDate = it
                                            pickedEndDate = LocalDate.ofEpochDay(msToDays(it))
                                        }
                                        dtDialog = !dtDialog
                                        onEvent(SprintEvent.ChangeDates(
                                            start = dtPickerState.selectedStartDateMillis ?: System.currentTimeMillis(),
                                            end = dtPickerState.selectedEndDateMillis
                                                ?: (System.currentTimeMillis() + daysToMs(
                                                    state.duration
                                                )),
                                            dur = dx(
                                                dtPickerState.selectedStartDateMillis,
                                                dtPickerState.selectedEndDateMillis
                                            ),
                                            cd = state.duration - state.elapsed
                                        ))
                                    }
                                ) {
                                    Text("Confirm Selection")
                                }
                            },
                            dismissButton = {
                                FilledTonalButton(
                                    onClick =
                                    {
                                        dtDialog = !dtDialog
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DateRangePicker(
                                state = dtPickerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f),
                                dateFormatter = remember { DatePickerFormatter() },
                                dateValidator = {
                                    it >= System.currentTimeMillis() - daysToMs(2) &&
                                    dxBool(
                                        dtPickerState.selectedStartDateMillis,
                                        dtPickerState.selectedEndDateMillis
                                    )
                                },
                                title = { Text("Sprint Calendar") },
                                headline = { Text("Select Sprint Start and End Dates",) },
                                showModeToggle = true,
                                colors = DatePickerDefaults.colors(

                                )
                            )
                        }
                }

            } else if (tymDialog) {

                Column {
                    Dialog(
                        onDismissRequest = {
                            tymDialog = !tymDialog
                        },
                    ) {
                        TimePicker(
                            state = dtTimePickerState,
                            modifier = Modifier
                                .fillMaxSize(),
                            layoutType = TimePickerLayoutType.Vertical,
                            colors = TimePickerDefaults.colors(

                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxHeight(0.2f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom

                        ){
                            FilledTonalButton(
                                onClick = {
                                    tymDialog = !tymDialog
                                    onEvent(SprintEvent.SetMeetingTime(dtTimePickerState.hour, dtTimePickerState.minute))
                                }
                            ) {
                                Text ("Confirm")
                            }
                        }

                    }
                }
            } else if (tym2Dialog) {

                Column {
                    Dialog(
                        onDismissRequest = {
                            tym2Dialog = !tym2Dialog
                        },
                    ) {
                        TimePicker(
                            state = dtTimePicker2State,
                            modifier = Modifier
                                .fillMaxSize(),
                            layoutType = TimePickerLayoutType.Vertical,
                            colors = TimePickerDefaults.colors(

                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxHeight(0.2f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom

                        ){
                            FilledTonalButton(
                                modifier = Modifier
                                    .fillMaxHeight(0.2f),
                                onClick = {
                                    tym2Dialog = !tym2Dialog
                                    onEvent(SprintEvent.SetReviewTime(dtTimePicker2State.hour, dtTimePicker2State.minute))
                                }
                            ) {
                                Text ("Confirm")
                            }
                        }
                    }
                }
            }
            else {

//                val u: String = "01 JAN 0000"
//                val v: String = "30 APR 2023"
//                val w: LocalDate = LocalDate.now()
//                fun formatDate(dt: LocalDate?): String? = run {
//                    DateTimeFormatter
//                        .ofPattern("yyyy mm dd")
//                        .format(dt)
//                }
//                val x = formatDate(w)
//                val y = LocalDate.parse(x, DateTimeFormatter.)
//                val z = LocalDate.parse(v, DateTimeFormatter.RFC_1123_DATE_TIME)
//
//
//                Text("$w \n $x \n $y \n $z \n")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.9375f),
                label = {
                    androidx.compose.material.Text(
                        text = "Comments",
                        color = MidnightBlue,
                    ) },
                placeholder = {
                    androidx.compose.material.Text(
                        text = "Enter a comment . . .  ",
                        color = NightBlue
                    ) },
                value = state.newComment,
                onValueChange = {
                    onEvent(SprintEvent.EditComment(it, state.user))
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (canComment) Icons.Default.Comment else Icons.Default.CommentsDisabled,
                        contentDescription = "Comments",
                        modifier = Modifier.clickable(
                            enabled = canComment
                        ) {
                            scope.launch {
                                onEvent(SprintEvent.SaveComment(state.newComment, state.user))
                            }
                        }
                    )
                }
                ,
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
            Spacer(modifier = Modifier.height(6.dp))

            var expanded by remember { mutableStateOf(false) }
            val items = floatArrayOf(0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f, 0.4f, 0.45f, 0.5f, 0.55f,
                0.6f, 0.65f, 0.7f, 0.75f, 0.8f, 0.85f, 0.875f, 0.9f, 0.925f, 0.95f, 0.97f, 0.99f, 1f)
            var selectedIndex by remember { mutableStateOf(0) }
            Row(modifier = Modifier
                //.wrapContentSize(Alignment.Center)
                .padding(all = 25.dp)
            ) {
                FilledTonalButton(
                    modifier = Modifier
                        .clip(CircleShape),
                    onClick =
                    {
                        tymDialog = !tymDialog
                    }
                ) {
                    Text("Scrum Meeting Time")
                }
                FilledTonalButton(
                    modifier = Modifier
                        .clip(CircleShape),
                    onClick =
                    {
                        tym2Dialog = !tym2Dialog
                    }
                ) {
                    Text("Schedule Sprint Review")
                }

            }
            Spacer(modifier = Modifier.height(4.dp))
            Divider()
            Spacer(modifier = Modifier.height(4.dp))

            var expanded3 by remember { mutableStateOf(false) }
            val items3 = state.users
            var selectedIndex3 by remember { mutableStateOf(0) }
            var textSize by remember { mutableStateOf(Size.Zero) }
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
                    value = state.owner.orEmpty(),
                    label = { androidx.compose.material.Text("Owner") },
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
                            enabled = !usr.isDisabled && ((usr.isAdmin || state.user?.isAdmin == true || state.user?.uid == usr.uid) ||
                                    (state.ownerUid == usr.uid || usr.isVerified && usr.isPowerUser)),
                            onClick = {
                                selectedIndex3 = index
                                expanded3 = false
                                onEvent(
                                    SprintEvent.ChangeOwner(
                                        usr.uid!!,
                                        usr.id!!,
                                        usr.photo.orEmpty(),
                                        usr.name?: "Unnamed Assignee #${usr.id}",
                                        usr
                                ))
                            //viewModel.onEvent(AddEditTaskEvent.ChangeAssignee(s.uid!!, s.userId!!, s.name?: s.email?: "Unnamed Assignee #${s.id}"))
                        }) {
                            androidx.compose.material.Text(text = items3[index].name ?: items3[index].email ?: "Unnamed Assignee #${usr.id}")
                        }
                    }
                }
            }
            Divider()
            var expanded4 by remember { mutableStateOf(false) }
            var selectedIndex4 by remember { mutableStateOf(0) }
            var textSize2 by remember { mutableStateOf(Size.Zero) }
            val icon2 = if (expanded4) Icons.Filled.ArrowDropDownCircle
            else Icons.Filled.ArrowDropDown
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
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
                    value = state.manager?: "None",
                    label = { androidx.compose.material.Text("Manager") },
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
//                            enabled = usr.isAdmin || (usr.isVerified && usr.isPowerUser && !usr.isDisabled),
                            enabled = !usr.isDisabled && ((usr.isAdmin || state.user?.isAdmin == true || state.user?.uid == usr.uid) ||
                                    (state.ownerUid == usr.uid || usr.isVerified && usr.isPowerUser)),
                            onClick = {
                                selectedIndex4 = index
                                expanded4 = false
                                onEvent(
                                    SprintEvent.ChangeManager(
                                    usr.uid!!,
                                    usr.id!!,
                                    usr.photo.orEmpty(),
                                    usr.name?: "Unnamed Assignee #${usr.id}",
                                    usr
                                ))
                        }) {
                            val disabledText = if (!expanded4) {
                                " (Disabled)"
                            } else {
                                ""
                            }
                            androidx.compose.material.Text(text = items3[index].name ?: items3[index].email ?: "Unnamed Assignee #${usr.id}")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            TextField(
                modifier = Modifier
                    .padding(PaddingValues(0.dp, 0.dp, 0.dp, padding.calculateBottomPadding()))
                    .wrapContentSize(Alignment.BottomCenter)
                    .onGloballyPositioned { },
                value = state.resolution.orEmpty(),
                label = { Text(
                    text = "Resolution",
                    color = MidnightPurple
                )},
                onValueChange = {
                    onEvent(SprintEvent.ChangeResolution(it))
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
            Spacer(modifier = Modifier.height(3.dp))
            Divider()
            Spacer(modifier = Modifier.height(3.dp))

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