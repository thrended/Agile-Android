package com.example.agileandroidalpha.feature_board.presentation.tasks

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.util.TaskOrder
import com.example.agileandroidalpha.firebase.firestore.model.Sprint

sealed class TasksEvent {
    data class Order(val taskOrder: TaskOrder): TasksEvent()
    data class AddSprint(val sprint: SprintRoom): TasksEvent()
    data class AddTask(val sprint: SprintRoom, val task: Task, val subtasks: List<Subtask>? = null): TasksEvent()
    data class DeleteTask(val task: Task, val subtasks: List<Subtask>? = null): TasksEvent()
    data class EditTask(val task: Task): TasksEvent()
    data class AddSubTask(val task: Task, val subtask: Subtask): TasksEvent()
    data class DeleteSubtask(val subtask: Subtask): TasksEvent()
    data class DragSubtask(val task: TaskAndSubtasks, val sub: Subtask, val stat: String, val bool: Boolean): TasksEvent()
    data class EditSubTask(val task: Task, val subtask: Subtask): TasksEvent()
    data class MarkComplete(val task: TaskAndSubtasks): TasksEvent()
    data class MarkSubtask(val task: TaskAndSubtasks, val subtask: Subtask, val bool: Boolean): TasksEvent()
    data class ChangeSprint(val sprint: Sprint): TasksEvent()
    data class RenameSprint(val name: String): TasksEvent()
    object RestoreTask: TasksEvent()
    object RestoreSubtask: TasksEvent()
    object ToggleOrderSection: TasksEvent()
    object ToggleAllCompleted: TasksEvent()
}