package com.example.agileandroidalpha.feature_board.presentation.admin

import com.example.agileandroidalpha.feature_board.domain.model.User

sealed class AdminEvent {
    data class AddUser(val user: User): AdminEvent()
    data class AddTestUser(val user: User): AdminEvent()
    object WipeData: AdminEvent()
    object ManageUsers: AdminEvent()
}
