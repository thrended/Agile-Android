package com.example.agileandroidalpha.feature_board.presentation.add_edit_task.components

import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.User

data class TaskSubtaskData (
    val id: Long?  = null,
    val task: Task? = null,
    val users: List<User>? = null,
    val subtasks: List<Subtask>? = null,
    val selectedSubtask: Subtask? = null,
    val selectedSubtaskId: Long? = null,
) {
}