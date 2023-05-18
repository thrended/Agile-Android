package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class SubTaskViewModel : ViewModel() {
    private val _tasks = getSubTasks().toMutableStateList()
    val tasks: List<SubTaskOld>
        get() = _tasks

    fun remove(item: SubTaskOld) {
        _tasks.remove(item)
    }
    fun changeTaskChecked(item: SubTaskOld, checked: Boolean) =
        tasks.find { it.id == item.id }?.let { task -> task.checked = checked
    }
    fun changeStatus(item: SubTaskOld, status: String) =
        tasks.find { it.id == item.id }?.let { task -> task.status = status
    }
    fun edit(item: SubTaskOld) {

    }
}
// should be accessible by parent Task
private fun getSubTasks() = List(100) { i -> SubTaskOld(
    TaskTmp(1, "A Task", "A task_subtask description"),
    i,
    "Hunt PGF # ${i+1}",
    "NM subtask",
    )
}