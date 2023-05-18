package com.example.agileandroidalpha.feature_board.domain.use_case

import com.example.agileandroidalpha.feature_board.domain.use_case.admin.LoadUsers
import com.example.agileandroidalpha.feature_board.domain.use_case.admin.ModifyUser

data class UserUseCases(
    val loadUsers: LoadUsers,
    val modifyUser: ModifyUser
)
