package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TasksList(
    modifier: Modifier = Modifier,
    list: List<TaskTmp>,
    listSub: List<SubTaskOld>,
    onCheckedTask: (TaskTmp, Boolean) -> Unit,
    onStatusClicked: (TaskTmp) -> Unit,
    onAddTask: (TaskTmp, String) -> Unit,
    onEditTask: (TaskTmp, String) -> Unit,
    onRenameTask: (TaskTmp, String) -> Unit,
    onAddSub: (TaskTmp) -> Unit,
    onCloseTask: (TaskTmp) -> Unit,
    onChecked: (SubTaskOld, Boolean) -> Unit,
    onEdit: (SubTaskOld) -> Unit,
    onClose: (SubTaskOld) -> Unit
) {
//    CollapsableLazyColumn(
//        sections = listOf(
//
//            CollapsableTask(
//                task_subtask, task_subtask.getSubTasks()
//            )
//        )
//    )
    LazyColumn(
        modifier = modifier
    ) {
        items(items = list,
            key = { task -> task.id }
        ) { task ->
            TaskItemOLD(
                taskID = task.id,
                taskIDString = task.id.toString(),
                freeTaskID = list.size.toString(),
                taskName = task.name,
                taskDesc = task.desc,
                taskPri = task.priority,
                checked = task.checked,
                status = task.status,
                listSub = task.getSubTasks(),
                onCheckedChange = { checked -> onCheckedTask(task, checked) },
                onStatusChange = { onStatusClicked(task) },
                onAddSubTask = { onAddSub(task) },
                onDraggedChange = {}, // insert drag logic here
                onAddTask = { onAddTask(task, list.size.toString()) },
                onEditTask = { onEditTask(task, task.id.toString()) }, // GO to edit screen
                onRenameTask = { taskName -> onRenameTask(task, taskName) },
                onCloseTask = { onCloseTask(task) }
            )
            SubTasksList(
                modifier = Modifier,
                list = task.getSubTasks(),
                onChecked,
                onEdit,
                onClose
            )
        }
    }
}

/*
            LazyRow(
                modifier = modifier
            ) {
                items(items = listSub,
                key = {task_subtask -> task_subtask.id}
                ) {
                    SubTaskItem(
                        taskName = task_subtask.name,
                        checked = task_subtask.checked,
                        status = task_subtask.status,
                        onCheckedSTChange = { checked -> onChecked(task_subtask, checked) },
                        onDraggedSTChange = {},
                        onEdit = {}, // GO to edit screen
                        onClose = { onClose(task_subtask) })
                }
            }*/

