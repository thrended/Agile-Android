package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.agileandroidalpha.Priority

class NewTaskViewModel (private val state: SavedStateHandle) : ViewModel() {
    val taskId: String = checkNotNull(state["taskId"])
    private var _init = TaskTmp(taskId.toInt(), "New Task", "New Description")
    var currentTask by mutableStateOf(_init)

    fun create(task: TaskTmp) {
        currentTask = task
    }

    fun delete(task: TaskTmp) {

    }
    fun getState() : SavedStateHandle = state

    fun saveState() : SavedStateHandle {

        return state
    }

    fun setName(task: TaskTmp, name: String) {
        task.name = name
    }

    fun setDesc(task: TaskTmp, desc: String) {
        task.desc = desc
    }

    fun setPriority(task: TaskTmp, priority: Priority) {
        task.priority = priority
    }

    fun save(task: TaskTmp) : TaskTmp {
        return task
    }
}