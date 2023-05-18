package com.example.agileandroidalpha.feature_board.presentation.add_edit_task

data class TaskTextFieldState(
    val id: Long? = null,
    val uid: String? = null,
    val text: String = "",
    val misc: String = "",
    val num: Int = 0,
    val hint: String = "",
    val isHintVisible: Boolean = true
)
