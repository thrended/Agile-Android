package com.example.agileandroidalpha.feature_board.domain.use_case.admin

import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class WipeData (
    private val repository: RoomRepo
) {
    suspend operator fun invoke() {
        try {
            repository.nukeBoard()
            repository.nukeSprints()
            repository.nukeUsers()
            repository.nukeTasks()
            repository.nukeSubtasks()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}