package com.example.agileandroidalpha.feature_board.domain.use_case

import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.LoadActiveSprint
import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.LoadSprints

data class BoardUseCases (
    val loadSprints: LoadSprints,
    val loadActiveSprint: LoadActiveSprint
) {
}