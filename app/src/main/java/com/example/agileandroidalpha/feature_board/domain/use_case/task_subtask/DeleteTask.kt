package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class DeleteTask(
    private val repo: RoomRepo
) {

    suspend operator fun invoke(task: Task, subtasks: List<Subtask>? = null) {
        repo.deleteTask(task)
        if (!subtasks.isNullOrEmpty()) {
            repo.deleteSubs(subtasks)//.map { s -> s.subtask })
        }
    }
}