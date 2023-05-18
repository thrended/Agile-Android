package com.example.agileandroidalpha.feature_board.presentation.admin

import com.example.agileandroidalpha.feature_board.domain.model.User
import com.example.agileandroidalpha.feature_board.domain.model.UserBrief
import com.example.agileandroidalpha.feature_board.domain.model.UserWithSprintsAndTasks

data class AdminState(
    val id: Long? = null,
    val userIDs: List<Long?> = emptyList(),
    val userData: List<UserWithSprintsAndTasks> = emptyList(),
    val users: List<User> = emptyList(),
    val usersBrief: List<UserBrief> = emptyList(),
    val testUserData: List<Pair<String,String>> = listOf( Pair("MGUD","MGUD"), Pair("hy","gud mrg"),
        Pair("Test user 1","password"), Pair("Test user 2","password2"), Pair("Brown", "Line Bear"),
        Pair("Test user 4","password3"), Pair("Test user 4","password4"), Pair("adm", "adm"),
        Pair("Administrator", "Administrator"), Pair("root", "root")
    ),
)
