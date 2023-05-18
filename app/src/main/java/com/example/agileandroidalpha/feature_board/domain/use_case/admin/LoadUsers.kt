package com.example.agileandroidalpha.feature_board.domain.use_case.admin

import com.example.agileandroidalpha.feature_board.domain.model.UserWithSprintsAndTasks
import com.example.agileandroidalpha.feature_board.domain.model.UserWithTasksAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import kotlinx.coroutines.flow.Flow

class LoadUsers(
    private val repository: RoomRepo
) {
    suspend operator fun invoke() : List<UserWithTasksAndSubtasks>? {
        return repository.getUsers()
    }

    fun showAll(): Flow<List<UserWithSprintsAndTasks>> {
        return repository.loadUsersAbc()
    }

    suspend fun count(): Int {
        return repository.countUsers()
    }
}