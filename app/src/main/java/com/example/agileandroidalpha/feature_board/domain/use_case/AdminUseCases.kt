package com.example.agileandroidalpha.feature_board.domain.use_case

import com.example.agileandroidalpha.feature_board.domain.use_case.admin.AddUser
import com.example.agileandroidalpha.feature_board.domain.use_case.admin.LoadUsers
import com.example.agileandroidalpha.feature_board.domain.use_case.admin.ModifyUser
import com.example.agileandroidalpha.feature_board.domain.use_case.admin.WipeData

data class AdminUseCases(
    val addUser: AddUser,
    val loadUsers: LoadUsers,
    val modifyUser: ModifyUser,
    val wipeData: WipeData
)
