package com.example.agileandroidalpha.firebase.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val repo: AuthRepo
) : ViewModel() {

    val currentUser = repo.user
    var user by mutableStateOf(repo.user)
    val anon = repo.isAnon()
    val loggedIn: Boolean
        get() = repo.isLoggedIn()
    var state by mutableStateOf(LoginState())
        private set
    //private val _state by mutableStateOf(LoginState())
    //    val state: LoginState = _state
    //private val _user by mutableStateOf(repo.user)
    //    val user : FirebaseUser? = _user

    fun createUser(
        valid: Boolean = validate(0),
        onCreate: (Triple<Boolean, String, String>) -> Unit
    ): Triple<Boolean, String, String> {
        var x = false
        viewModelScope.launch {
            try {
                if (!valid) {
                    throw IllegalArgumentException("Must enter a valid username and password. " +
                    "Username must be a valid email address and between 3 and 33 characters. +" +
                            "Password must contain between 8 and 64 characters .")
                }
                else {
                    state = state.copy(verifying = true)
                    x = repo.registerUserWithEmail(state.regUser, state.regPass) { gud ->
                        state = state.copy(isSuccessful = gud)
                        Log.d("login", "Registered credentials: ${state.username} :: ${state.password}")
                        Log.d("login", "gud = $gud, x = $x")
                        onCreate(Triple(gud, state.username, state.password))
                    }
                }
            } catch(e: Exception) {
                state = state.copy(regError = e.localizedMessage)
                e.printStackTrace()
            } finally {
                state = state.copy(verifying = false)
            }
        }
        return Triple(x || state.isSuccessful, state.regUser, state.regPass)

    }

    fun login(
        valid: Boolean = validate(1),
        onLogin: (Triple<Boolean, String, String>) -> Unit
    ): Triple<Boolean, String, String> {
        var x = false
        viewModelScope.launch {
            try {
                if (!valid) {
                    throw IllegalArgumentException("Must enter a valid username and password. " +
                            "Username must be between 3 and 33 characters and contain no spaces." +
                            "Password must be between 6 and 64 characters and contain at least " +
                            "one uppercase letter, one lower case letter and one number.")
                }
                else {
                    state = state.copy(verifying = true)
                    x = repo.loginWithEmailAndPassword(state.username, state.password) { gud ->
                        state = state.copy(isSuccessful = gud)
                        Log.d(
                            "login",
                            "Entered credentials: ${state.username} :: ${state.password}"
                        )
                        Log.d("login", "gud = $gud, x = $x")
                        onLogin.invoke(Triple(gud, state.username, state.password))
                    }
                }
            } catch(e: Exception) {
                state = state.copy(regError = e.localizedMessage)
                e.printStackTrace()
            } finally {
                state = state.copy(verifying = false)
            }
        }
        return Triple(x || state.isSuccessful, state.username, state.password)
    }

    fun signOut() = repo.signOut()

    fun anonLogin() {
        viewModelScope.launch {
            try {
                state = state.copy(verifying = true)
                repo.signInAnonymously()
            } catch(e: Exception) {
                state = state.copy(regError = e.localizedMessage)
                e.printStackTrace()
            } finally {
                state = state.copy(verifying = false)
            }

        }
    }

    private fun reload() {
        user!!.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUI(user)
                //Toast.makeText(context, "Reload successful!", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("LoginVM", "reload", task.exception)
                //Toast.makeText(context, "Failed to reload user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            state = state.copy(

            )
        }
        else {

        }
    }

    fun changeLoginText(text: String, n: Int = 0){
        when (n) {
            0 -> {
                state = state.copy(
                    username = text
                )
            }
            1 -> {
                state = state.copy(
                    password = text
                )
            }
            else -> {
                state = state.copy(
                    password = text
                )
            }
        }
    }

    fun changeRegisterText(text: String, n: Int = 0){
        when (n) {
            0 -> {
                state = state.copy(
                    regUser = text
                )
            }
            1 -> {
                state = state.copy(
                    regPass = text
                )
            }
            2 -> {
                state = state.copy(
                    confirmPass = text
                )
            }
            else -> {
                state = state.copy(
                    confirmPass = text
                )
            }
        }
    }

    private fun validate(n: Int = 1) : Boolean {
        return when(n) {
            0 -> {  // validate(0) -> create user
                state.regUser.isNotBlank() && state.regPass.isNotBlank()
                    && state.confirmPass == state.regPass
                    //state.regUser.trim() == state.regUser //&& state.regPass.trim() == state.regPass
                    //&& state.regUser.matches(Regex.fromLiteral("^[a-zA-Z0-9+_-]+@[a-zA-Z0-9.-]+"))
                    //&& state.regPass.matches(Regex.fromLiteral("^([^0-9]*|[^A-Z]*|[^a-z]*|[a-zA-Z0-9]*)$"))
                    //&& state.regUser.length in 3..33 && state.regPass.length in 6..64
                    //&& state.confirmPass == state.regPass
            }
            1 -> {  // validate(1) -> login
                state.username.isNotBlank() && state.password.isNotBlank()
                        //state.username.length in 3..33 && state.password.length in 6..66
            }
            2 -> {  // validate(2) ->
                state.username.isNotBlank() && state.password.isNotBlank()
            }
            else -> {
                false
            }
        }
    }

}