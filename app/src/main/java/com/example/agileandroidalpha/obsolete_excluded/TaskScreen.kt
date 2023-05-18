package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    taskViewModel: OldTaskViewModel = viewModel(),
    state: SavedStateHandle = SavedStateHandle(),
    subtaskViewModel: SubTaskViewModel = viewModel(),
    onAddTask: (TaskTmp, String) -> Unit = { _, _ -> },
    onEditTask: (TaskTmp, String) -> Unit = { _, _ -> },
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = "Agile Droid",
//                icon = Icons.Filled.Task,
//                onIconClick = {
//                    coroutineScope.launch {
//                        scaffoldState.drawerState.open()
//                    }
//                }
//            )
//         },
//        scaffoldState = scaffoldState,
//        drawerContent = {
//            AGDroidDrawer(
//                currentScreens = Screens.Tasks.route,
//                closeDrawerAction = {
//                    // here - Drawer close
//                    coroutineScope.launch {
//                        scaffoldState.drawerState.close()
//                    }
//                },
//            )
//        },
        content = { padding ->
        TasksList(modifier = Modifier.padding(padding),
            list = taskViewModel.tasks,
            listSub = subtaskViewModel.tasks,
            onCheckedTask = { task, checked -> taskViewModel.changeTaskChecked(task, checked) },
            onStatusClicked = { task -> taskViewModel.changeStatus(task) },
            onAddSub = { task -> taskViewModel.addSubTask(task) }, // improve this later
            onAddTask = onAddTask,
            onEditTask = onEditTask,
            onRenameTask = { task, name -> taskViewModel.rename(task, name) },
            onCloseTask = { task -> taskViewModel.remove(task) },
            onChecked = { task, checked -> subtaskViewModel.changeTaskChecked(task, checked) },
            onEdit = {},
            onClose = { subtask -> subtaskViewModel.remove(subtask) }
        )
//        Row(modifier = modifier) {
//            SubTasksList(
//                list = subtaskViewModel.tasks,
//                onChecked = { task_subtask, checked -> subtaskViewModel.changeTaskChecked(task_subtask, checked) },
//                onEdit = {},
//                onClose = { task_subtask -> subtaskViewModel.remove(task_subtask) }
//            )
//        }
    }
    )
}