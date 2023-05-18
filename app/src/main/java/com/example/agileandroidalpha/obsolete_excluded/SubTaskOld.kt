package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.agileandroidalpha.Priority

class SubTaskOld (
    val parent: TaskTmp,
    val id: Int,
    val name: String = "A SubTask",
    val desc: String = "SubTask Description",
    val subLabels: Set<String>? = null,
    pID: Int = parent.getTaskId(),
    pName: String = parent.getTaskName(),
    initialChecked: Boolean = false,
    initialPriority: Priority = Priority.Lowest,
    initialStatus: String = "TO DO"
) {
    fun getSTId(): Int { return id }
    fun getSTName(): String { return name}
    fun getSTDesc(): String { return desc }
    fun getSTParent(): TaskTmp { return parent }
    fun getSTPriority(): Priority { return priority }
    fun getSTLabels(): Set<String> { return (subLabels!!) }
    fun getSTBoard(): ScrumModel? { return parent.getTaskBoard()!! }
    fun getRelatedSubtasks(): List<SubTaskOld>? { return parent.getSubtasks() }
    fun getSTChecked(): Boolean { return checked }
    fun getSTStatus(): String { return status }
    var checked by mutableStateOf(initialChecked)
    var priority by mutableStateOf(initialPriority)
    var status: String by mutableStateOf(initialStatus)
}