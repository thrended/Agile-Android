package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class AddSubtasks (
    private val repo: RoomRepo
) {

    suspend operator fun invoke(subtasks: List<Subtask>? =null) {
        repo.upsertSubtasks(subtasks)
        //repo.insert(TaskWithSubtasks(taskId = id, subId = x))
    }
}