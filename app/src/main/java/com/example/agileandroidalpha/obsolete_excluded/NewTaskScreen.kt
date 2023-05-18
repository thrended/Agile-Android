package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.R

@Composable
fun NewTaskScreen(
    modifier: Modifier = Modifier,
    newTaskViewModel: NewTaskViewModel = viewModel(),
    state: SavedStateHandle = newTaskViewModel.getState(),
    taskID: String = newTaskViewModel.taskId,
    task: TaskTmp = TaskTmp(taskID.toInt(), "New Task", "New Description"),
    onCancel: () -> Unit = {},
    onSave: (TaskTmp) -> Unit = {}
) {
    newTaskViewModel.create(task)
    Column(modifier = Modifier
            .padding(all = 10.dp),
        verticalArrangement = Arrangement.SpaceAround){

        OutlinedTextField(
            value = "",
            onValueChange = { name -> newTaskViewModel.setName(task, name) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            singleLine = true,
            label = { Text("Task Name") },
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

        Box(){
            OutlinedTextField(
                value = "",
                onValueChange = { desc -> newTaskViewModel.setDesc(task, desc) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                maxLines = 5,
                label = { Text("Describe your task_subtask") },
                modifier = Modifier.padding(10.dp),
                textStyle = TextStyle(fontWeight = FontWeight.Bold,
                    fontSize = 10.sp),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.redbox),
                        contentDescription = "red box",
                        modifier = Modifier
                            .size(20.dp)
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
        }
        var expanded by remember { mutableStateOf(false) }
        val items = enumValues<Priority>().joinToString(",").split(",")
        var selectedIndex by remember { mutableStateOf(0)
        }
        Box(modifier = Modifier
            .wrapContentSize(Alignment.TopStart)){
            Text("Priority: ${items[selectedIndex]}",modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .background(
                    Color.LightGray
                ))
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.Cyan
                    )
            ) {
                items.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        expanded = false
                    }) {
                        val disabledText = if (!expanded) {
                            " (Disabled)"
                        } else {
                            ""
                        }
                        Text(text = s + disabledText)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(all = 50.dp))
        Box()
        {
            OutlinedTextField(
                value = "",
                onValueChange = { desc -> newTaskViewModel.setDesc(task, desc) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                maxLines = 5,
                label = { Text("Add Labels") },
                modifier = Modifier.padding(10.dp),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.redbox),
                        contentDescription = "red box",
                        modifier = Modifier
                            .size(20.dp)
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
        }
    }
    Spacer(modifier = Modifier.height(200.dp))
    Row(modifier = Modifier
        .padding(all = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        FloatingActionButton(onClick = {
            newTaskViewModel.save(task)
            onSave(task)
        }) {
            Icon(
                Icons.Filled.Save,
                contentDescription = "Save",
                modifier = Modifier
                    .size(40.dp)
            )
        }
        FilledTonalButton(onClick = {
            newTaskViewModel.delete(task)
            onCancel()
        }) {
            Icon(
                Icons.Filled.Cancel,
                contentDescription = "Save",
                modifier = Modifier
                    .size(40.dp)
            )
        }
    }
}