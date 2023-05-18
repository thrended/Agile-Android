package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class DeleteSubtask (
    private val repo: RoomRepo
) {

    suspend operator fun invoke(subtask: Subtask) {
        repo.deleteSubtask(subtask)
    }
}