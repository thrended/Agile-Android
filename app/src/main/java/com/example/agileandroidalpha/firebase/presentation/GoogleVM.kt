package com.example.agileandroidalpha.firebase.presentation

import androidx.lifecycle.ViewModel
import com.example.agileandroidalpha.firebase.login.GoogleSignInState
import com.example.agileandroidalpha.firebase.login.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GoogleVM @Inject constructor(

) : ViewModel() {
    private val _state = MutableStateFlow(GoogleSignInState())
    val state = _state.asStateFlow()

    fun onLoginResult(result: LoginResult) {
        _state.update { it.copy(
            isSuccessful = result.data != null,
            error = result.msg,
            newUser = result.newUser
        ) }
    }

    fun reset() {
        _state.update { GoogleSignInState() }
    }
}