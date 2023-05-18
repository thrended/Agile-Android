package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class GetTask (
    private val repo: RoomRepo
) {

    suspend fun grab(id: Long): Task? {
        return repo.getTaskById(id)
    }

    suspend operator fun invoke(id: Long): TaskAndSubtasks? {
        return repo.getTaskSingle(id)
    }
}