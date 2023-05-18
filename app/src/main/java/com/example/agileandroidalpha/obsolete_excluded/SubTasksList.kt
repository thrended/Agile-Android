package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SubTasksList(
    modifier: Modifier = Modifier,
    list: List<SubTaskOld>,
    onChecked: (SubTaskOld, Boolean) -> Unit,
    onEdit: (SubTaskOld) -> Unit,
    onClose: (SubTaskOld) -> Unit
) {
    LazyRow(
        modifier = modifier
    ) {
        items(items = list,
            key = { task -> task.id }
        ) { task ->
            task.status?.let {
                SubTaskItemOLD(
                    taskName = task.name,
                    checked = task.checked,
                    status = it,
                    onCheckedSTChange = { checked -> onChecked(task, checked) },
                    onDraggedSTChange = {},
                    onEdit = {}, // GO to edit screen
                    onClose = { onClose(task) })
            }
        }
    }
}

