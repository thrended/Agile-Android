package com.example.agileandroidalpha.feature_board.domain.use_case.sprint

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class QuickAddSprint(
    private val repository: RoomRepo
) {
    suspend operator fun invoke(sprint: SprintRoom) : Long {
        return repository.upsertSprint(sprint)
    }

    suspend fun update(sprint: SprintRoom) {
        repository.updateSprint(sprint)
    }

    suspend fun update(id: Long) {
        repository.getSprintById(id)?.let { repository.updateSprint(it) }
    }
}