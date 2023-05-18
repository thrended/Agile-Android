package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.agileandroidalpha.Priority

class OldTaskViewModel (private val state: SavedStateHandle) : ViewModel() {

    private val _tasks = getTasks().toMutableStateList()
    private val _nextId: MutableLiveData<String> = MutableLiveData<String>("${tasks.size}")
    private val _text: MutableLiveData<String> = MutableLiveData<String>("txt")

    val nextId: LiveData<String>
        get() = _nextId
    val tasks: List<TaskTmp>
        get() = _tasks
    val text: LiveData<String>
        get() = _text

    private fun getData() : MutableLiveData<String> = state.getLiveData(getFreeID())
    fun getIds(tasks: List<TaskTmp>) : List<String> {
        return List(tasks.size) {
                i -> "$i"
        }
    }
    private fun getFreeID() : String {
        return "${tasks.size}"
    }
    fun getState() : SavedStateHandle {
        return state
    }
    fun saveState() : SavedStateHandle {
        state["taskId"] = getData()
        return state
    }
    fun create(item: TaskTmp) {
        _tasks.add(item)
    }
    fun edit(item: TaskTmp) {

    }
    fun rename(item: TaskTmp, name: String) =
        tasks.find { it.id == item.id }?.let { task ->
            task.name = name
    }
    fun remove(item: TaskTmp) {
        _tasks.remove(item)
    }
    fun changeTaskChecked(item: TaskTmp, checked: Boolean) =
        tasks.find { it.id == item.id }?.let { task -> task.checked = checked
    }
    fun decPriority(item: TaskTmp) =
        tasks.find { it.id == item.id }?.let {
                task -> task.priority = (
                when (task.priority) {
                    Priority.Showstopper -> Priority.Critical
                    Priority.Critical -> Priority.Highest
                    Priority.Highest -> Priority.High
                    Priority.High -> Priority.Medium
                    Priority.Medium -> Priority.Low
                    Priority.Low -> Priority.Lowest
                    else -> Priority.Trivial
                }
                )
        }
    fun incPriority(item: TaskTmp) =
        tasks.find { it.id == item.id }?.let {
                task -> task.priority = (
                when (task.priority) {
                    Priority.Trivial -> Priority.Lowest
                    Priority.Lowest -> Priority.Low
                    Priority.Low -> Priority.Medium
                    Priority.Medium -> Priority.High
                    Priority.High -> Priority.Highest
                    Priority.Highest -> Priority.Critical
                    else -> Priority.Showstopper
                }
                )
        }
    fun changeStatus(item: TaskTmp) =
        tasks.find { it.id == item.id }?.let {
                task -> task.status = (
                when (task.status) {
                    "Open" -> "TO DO"
                    "TO DO" -> "In Progress"
                    "In Progress" -> "Fixing"
                    "Fixing" -> "Reviewing"
                    "Reviewing" -> "Done"
                    else -> "TO DO"
                }
                )
    }
    fun addSubTask(item: TaskTmp) =
        tasks.find { it.id == item.id }?.subtasks?.add(SubTaskOld(item, item.subtasks.size))

    fun expand(item: TaskTmp) {

    }
    fun printSubTasks(item: TaskTmp) {

    }
    fun setText(s: String) {
        _text.value = s
    }
}

private fun getTasks() = List(10) { i -> TaskTmp(
    i,
    "Hunt Red Boxes # ${i+1}",
    "Another pso pro hunt"
)
}