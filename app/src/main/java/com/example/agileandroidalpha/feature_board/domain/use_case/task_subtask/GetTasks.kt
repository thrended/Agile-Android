package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.SprintWithTasksAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.domain.util.OrderType
import com.example.agileandroidalpha.feature_board.domain.util.TaskOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTasks(
    private val repository: RoomRepo
) {

    operator fun invoke(
        id: Long,
        taskOrder: TaskOrder = TaskOrder.Date(OrderType.Descending)
    ): Flow<List<TaskAndSubtasks>> {
        return repository.getTasksBySprint(id).map { tasks ->
            when (taskOrder.type) {
                is OrderType.Ascending -> {
                    when(taskOrder) {
                        is TaskOrder.Priority -> tasks.sortedBy { it.task.priority.ordinal }
                        is TaskOrder.Title -> tasks.sortedBy { it.task.title.lowercase() }
                        is TaskOrder.Points -> tasks.sortedBy { it.task.points }
                        is TaskOrder.Date -> tasks.sortedBy { it.task.timestamp }
                        is TaskOrder.Assignee -> tasks.sortedBy { it.task.assignee }
                        is TaskOrder.Reporter -> tasks.sortedBy { it.task.reporter }
                        is TaskOrder.Creator -> tasks.sortedBy { it.task.createdBy }
                        is TaskOrder.Created -> tasks.sortedBy { it.task.creDate }
                        is TaskOrder.Accessed -> tasks.sortedBy { it.task.accDate }
                        is TaskOrder.Content -> tasks.sortedBy { it.task.content.lowercase() }
                        is TaskOrder.Color -> tasks.sortedBy { it.task.color }
                        is TaskOrder.ID -> tasks.sortedBy { it.task.taskId }
                        is TaskOrder.Size -> tasks.sortedBy { it.subtasks.size }
                        else -> tasks.sortedBy { it.task.priority.ordinal }
                    }
                }
                is OrderType.Descending -> {
                    when(taskOrder) {
                        is TaskOrder.Priority -> tasks.sortedByDescending { it.task.priority.ordinal }
                        is TaskOrder.Title -> tasks.sortedByDescending { it.task.title.lowercase() }
                        is TaskOrder.Points -> tasks.sortedByDescending { it.task.points }
                        is TaskOrder.Date -> tasks.sortedByDescending { it.task.timestamp }
                        is TaskOrder.Assignee -> tasks.sortedByDescending { it.task.assignee }
                        is TaskOrder.Reporter -> tasks.sortedByDescending { it.task.reporter }
                        is TaskOrder.Creator -> tasks.sortedByDescending { it.task.createdBy }
                        is TaskOrder.Created -> tasks.sortedByDescending { it.task.creDate }
                        is TaskOrder.Accessed -> tasks.sortedByDescending { it.task.accDate }
                        is TaskOrder.Content -> tasks.sortedByDescending { it.task.content.lowercase() }
                        is TaskOrder.Color -> tasks.sortedByDescending { it.task.color }
                        is TaskOrder.ID -> tasks.sortedByDescending { it.task.taskId }
                        is TaskOrder.Size -> tasks.sortedByDescending { it.subtasks.size }
                        else -> tasks.sortedByDescending { it.task.priority.ordinal }
                    }
                }
            }
        }
    }

    fun bySprint(
        id: Long, taskOrder: TaskOrder = TaskOrder.Date(OrderType.Descending)
    ): Flow<SprintWithTasksAndSubtasks> {
        return repository.getSprint(id)
    }

    suspend fun count(): Int {
        return repository.countTasks()
    }
}

// ): Flow<List<TaskAndSubtasks>> {
//        return repository.getTasksAndSubtasks().map { tasks ->
// ): Flow<List<TaskWithSubtasksTMP>> {
//        return repository.getTasksFull().map { tasks ->