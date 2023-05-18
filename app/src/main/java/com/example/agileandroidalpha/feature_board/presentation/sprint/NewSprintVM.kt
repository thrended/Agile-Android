package com.example.agileandroidalpha.feature_board.presentation.sprint

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.feature_board.domain.use_case.SprintUseCases
import com.example.agileandroidalpha.feature_board.presentation.sprint.components.SprintTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NewSprintVM @Inject constructor(
    private val sprintUseCases: SprintUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(SprintState())
    val state: State<SprintState> = _state

    private val _title = mutableStateOf(SprintTextFieldState(
        hint = "Enter a Sprint Title here..."
    ))
    val title: State<SprintTextFieldState> = _title

    private val _desc = mutableStateOf(SprintTextFieldState(
        hint = "Enter sprint description here..."
    ))
    val desc: State<SprintTextFieldState> = _title

    private val _start = mutableStateOf(SprintTextFieldState(
        date = Date()
    ))
    val start: State<SprintTextFieldState> = _start

    private val _end = mutableStateOf(SprintTextFieldState(

    ))
    val end: State<SprintTextFieldState> = _end

    private val _length = mutableStateOf(SprintTextFieldState(
        num2 = 21.0
    ))
    val length: State<SprintTextFieldState> = _length

    private val _owner = mutableStateOf(SprintTextFieldState(
        text = "Owner"
    ))
    val owner: State<SprintTextFieldState> = _owner

    private val _manager = mutableStateOf(SprintTextFieldState(
        text = "Manager"
    ))
    val manager: State<SprintTextFieldState> = _manager

    private val _startDate = mutableStateOf(Date())
    private val _endDate = mutableStateOf((Date()))
    val dates: Pair<State<Date>, State<Date>> = Pair(_startDate, _endDate)

    private var getSprintJob: Job? = null

    private var currentId: Long? = null

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            if (id != -1L) {
                viewModelScope.launch {
                    sprintUseCases.getSprint.async(id)?.also { s ->
                        currentId = s.sprint.sprintId
                        _state.value = state.value.copy(
                            id = s.sprint.sprintId,
                            boardId = s.sprint.boardId,
                            info = s.sprint.info,
                            room = s.sprint,
                            tasks = s.tasks,
                            roomUsers = s.users,


                        )
                        _title.value = title.value.copy(
                            text = s.sprint.title,
                            isHintVisible = false
                        )
                        _start.value = start.value.copy(

                        )
                        _startDate.value = dates.first.value
                        _endDate.value = dates.second.value
                    }
                }
            }
        }
    }

    private fun loadSprint() {

    }
}