package com.example.agileandroidalpha.feature_board.presentation.sprint.components

import java.util.Date

data class SprintTextFieldState(
    val text: String = "",
    val text2: String = "",
    val num: Long = 0,
    val num2: Double = 0.0,
    val date: Date = Date(),
    val date2: Date? = null,
    val bool: Boolean = false,
    val hint: String = "",
    val isHintVisible: Boolean = true
)