package com.example.agileandroidalpha.feature_board.presentation.users

data class UserState(
    val id: Long? = null,
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val photo: String? = null,
    val anon: Boolean = false,
    val emailVerified: Boolean = false,
    val providerId: String = "",
    val firebaseId: String = "",
    val tid: String? = null,
    val reAuthState: Boolean = false,
    val success: Boolean = false,
    val miscError: String? = null,
    val textError: String? = null,
    val emailError: String? = null,
)