package com.example.agileandroidalpha.firebase.login

data class LoginState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val regUser: String = "",
    val regPass: String = "",
    val confirmPass: String = "",
    val inputEmail: String = "",
    val verifying: Boolean = false,
    val isSuccessful: Boolean = false,
    val regError: String? = null,
    val textError: String? = null,
    val signInError: String? = null
)

data class GoogleSignInState(
    val isSuccessful: Boolean = false,
    val error: String? = null,
    val newUser: Boolean? = null
)