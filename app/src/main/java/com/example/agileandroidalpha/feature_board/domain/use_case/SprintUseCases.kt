package com.example.agileandroidalpha.feature_board.domain.use_case

import com.example.agileandroidalpha.feature_board.domain.use_case.admin.LoadUsers
import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.CloneSprint
import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.DeleteSprint
import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.GetSprint
import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.LoadSprints
import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.QuickAddSprint
import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.StartSprint
import com.example.agileandroidalpha.feature_board.presentation.sprint.AddEditSprint

data class SprintUseCases(
    val cloneSprint: CloneSprint,
    val quickAddSprint: QuickAddSprint,
    val startSprint: StartSprint,
    val deleteSprint: DeleteSprint,
    val loadSprints: LoadSprints,
    val addEditSprint: AddEditSprint,
    val getSprint: GetSprint,
    val loadUsers: LoadUsers
)
