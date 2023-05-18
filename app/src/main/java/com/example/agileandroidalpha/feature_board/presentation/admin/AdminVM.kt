package com.example.agileandroidalpha.feature_board.presentation.admin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.feature_board.domain.model.UserBrief
import com.example.agileandroidalpha.feature_board.domain.use_case.AdminUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminVM @Inject constructor(
    private val adminUseCases: AdminUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AdminState())
    val state: State<AdminState> = _state

    private val currentUserId: Long? = null

    private var job: Job? = null

    init {
        getUsers()
    }

    fun onEvent(event: AdminEvent) {
        when(event) {
            is AdminEvent.AddUser -> {
                viewModelScope.launch {
                    adminUseCases.addUser(event.user)
                }
            }
            is AdminEvent.AddTestUser -> {
                viewModelScope.launch {
                    adminUseCases.addUser(event.user)
                }
            }
            is AdminEvent.ManageUsers -> {
                viewModelScope.launch {
                    adminUseCases.loadUsers.showAll()
                }
            }
            is AdminEvent.WipeData -> {
                viewModelScope.launch {
                    adminUseCases.wipeData
                }
            }
        }
    }

    private fun getUsers() {
        job?.cancel()
        job = adminUseCases.loadUsers.showAll()
            .onEach { users ->
                _state.value = state.value.copy(
                    userIDs = users.map { u -> u.user.userId },
                    userData = users,
                    users = users.map { u -> u.user },
                    usersBrief = users.map { u -> UserBrief(
                        u.user.userId,
                        u.user.username,
                        u.user.password,
                        u.user.info.email,
                        u.user.info.privilegeLvl,
                        u.user.info.admin,
                        u.user.info.active
                    ) }

                )
            }
            .launchIn(viewModelScope)
    }
}