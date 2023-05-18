package com.example.agileandroidalpha.feature_board.domain.use_case.sprint

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import kotlinx.coroutines.flow.Flow

class GetSprint (
    private val repository: RoomRepo
){
    operator fun invoke(id: Long): Flow<SprintWithUsersAndTasks> {
        return repository.loadSprintFull(id)
    }

    suspend fun async(id: Long): SprintWithUsersAndTasks? {
        return repository.loadSprint(id)
    }

    suspend fun asyncBasic(id: Long): SprintRoom? {
        return repository.getSprintById(id)
    }

    fun basic(id: Long): Flow<SprintRoom> {
        return repository.loadSprintById(id)
    }
}