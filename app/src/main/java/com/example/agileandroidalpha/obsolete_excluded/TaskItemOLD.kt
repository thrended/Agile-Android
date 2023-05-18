package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agileandroidalpha.ImageCard
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.R
import kotlin.math.roundToInt

@Composable
fun TaskItemOLD(
    taskID: Int,
    taskIDString: String,
    freeTaskID: String,
    taskName: String,
    taskDesc: String,
    taskPri: Priority,
    listSub: List<SubTaskOld>,
    checked: Boolean,
    status: String,
    onCheckedChange: (Boolean) -> Unit,
    onAddSubTask: () -> Unit,
    onStatusChange: () -> Unit,
    onDraggedChange: () -> Unit,
    onAddTask: (String) -> Unit,
    onEditTask: (String) -> Unit,
    onRenameTask: (String) -> Unit,
    onCloseTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier, verticalAlignment = CenterVertically
    ) {
        IconButton(onClick = {onAddTask(freeTaskID)} ) {
            Icon(Icons.Filled.AddTask, contentDescription = "Add")
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            text = "$taskPri",
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            text = "taskID# $taskID $taskName"
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        IconButton(onClick = {onEditTask(taskIDString)} ) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = onCloseTask) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
    OutlinedTextField(
        value = taskName,
        onValueChange = onRenameTask,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        singleLine = true,
        label = { Text("Rename Task")},
        modifier = Modifier.padding(10.dp),
        textStyle = TextStyle(fontWeight = FontWeight.Bold,
            fontSize = 20.sp),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.redbox),
                contentDescription = "red box",
                modifier = Modifier
                    .size(40.dp)
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.redbox),
                contentDescription = "red box",
                modifier = Modifier
                    .size(40.dp)
            )
        }
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceAround)
    {
    OutlinedButton(
        onClick = onStatusChange,
        modifier = Modifier
            .width(150.dp)
            .padding(start = 25.dp),
        shape = RoundedCornerShape(15.dp),
    ) {
        Text(
            modifier = Modifier
                .weight(0.25f),
            text = status,
            textAlign = TextAlign.Center
        )
    }
        Text(
            modifier = Modifier
                .weight(0.25f)
                .padding(top = 12.dp, start = 16.dp),
            text = taskDesc
        )
    }
    val painter = painterResource(id = R.drawable.profsad)
    val painter2 = painterResource(id = R.drawable.profsad2)
    val painter3 = painterResource(id = R.drawable.alienhead)
    val painter4 = painterResource(id = R.drawable.drawer)
    val painter5 = painterResource(id = R.drawable.redbox)
    val description = "Welcome to the Library of Forbidden Guds"
    val title = "Sub-task_subtask"
    Box(modifier = modifier){
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(0.85f)
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
        )
        {
            ImageCard(
                painter = when (taskID % 5) {
                    0 -> painter
                    1 -> painter2
                    2 -> painter3
                    3 -> painter4
                    else -> painter5                        }
                , description,
                title = when (taskID % 5) {
                    0 -> "$title 1"
                    1 -> "Welcome to the Library of Forbidden Guds"
                    2 -> "SJS Hunt"
                    3 -> "Guld Hunt"
                    else -> "Red Box Syndrome"
                })
        }
    }
    Button(onClick = onAddSubTask) {
        Text(
            text = "Add Subtask",
            modifier = modifier.align(CenterVertically)
        )
    }
    if (listSub.isNotEmpty()) {
        Column(
            modifier = modifier, horizontalAlignment = CenterHorizontally
        ) {
            for (sub in listSub) {
                Box(modifier = modifier
                    .weight(0.25f)) {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp),
                        text = sub.name
                    )
                }
            }
        }
    }
}
