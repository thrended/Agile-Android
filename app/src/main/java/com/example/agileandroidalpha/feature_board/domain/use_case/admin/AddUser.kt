package com.example.agileandroidalpha.feature_board.domain.use_case.admin

import com.example.agileandroidalpha.feature_board.domain.model.User
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class AddUser(
    private val repository: RoomRepo
) {
    suspend operator fun invoke(user: User?) {
        repository.upsertUser(user)
    }
}