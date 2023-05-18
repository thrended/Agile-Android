package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import com.example.agileandroidalpha.Priority
import kotlin.random.Random

class TaskTmp (
    val id: Int,
    initialName: String,
    initialDesc: String,
    var labels: MutableSet<String>? = null,
    var board: ScrumModel? = null,
    //initialAssignedTo: UserOLD = DefaultUser(),
    //initialReporter: UserOLD = DefaultUser(),
    initialChecked: Boolean = false,
    initialStatus: String = "Open",
    initialSubtasks: MutableList<SubTaskOld> = mutableListOf(),
//    initialSubtasks: MutableList<SubTask> = MutableList(3) {
//        SubTask(Task(id,name,desc), id*10000, "$name.subtask1", desc );
//        SubTask(Task(id,name,desc), id*10000+1, "$name.subtask2", desc );
//        SubTask(Task(id,name,desc), id*10000+2, "$name.subtask3", desc );},
    initialPriority: Priority = generateRndPriority()
) {
    //constructor(id: Int) : this()
    //constructor(id: Int, name: String) : this()
    //constructor(id: Int, name: String, desc: String) : this()
    //constructor(id: Int, name: String, desc: String, priority: String) : this()
    //constructor(id: Int, name: String, subtasks: List<SubTask>): this()
    //constructor(id: Int, name: String, boardOLD: ScrumModel) : this()

    fun getTaskId(): Int { return id }
    fun getTaskName(): String { return name }
    fun getTaskDesc(): String { return desc }
    fun getTaskPriority(): Priority { return priority }
    fun getTaskLabels(): Set<String> { return (labels!!) }
    fun getTaskBoard(): ScrumModel? { return board!! }
    fun getSubtasks(): List<SubTaskOld> { return subtasks }
    fun getSubTasks(): List<SubTaskOld> { return subtasks }
    fun getTaskChecked(): Boolean { return checked }
    fun getTaskStatus(): String { return status }
    fun numSubTasks(): Int { return subtasks.size }

    var checked by mutableStateOf(initialChecked)
    var desc by mutableStateOf(initialDesc)
    var name by mutableStateOf(initialName)
    var priority by mutableStateOf(initialPriority)
    var status by mutableStateOf(initialStatus)
    var subtasks = initialSubtasks.toMutableStateList()
}



fun generateRndPriority(): Priority {
    val n = Random.nextInt(8)
    return enumValues<Priority>()[n]
}
