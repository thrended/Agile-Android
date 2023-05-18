package com.example.agileandroidalpha.firebase.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.MultiFactorInfo

data class LoginResult(
    val data: UserData?,
    val msg: String?,
    val newUser: Boolean? = null
) {

    companion object Option {
        @JvmStatic var loginActive by mutableStateOf(false)
        fun changeOnlineStatus() = run { loginActive = !loginActive }
    }
}

data class UserData(
    val uid: String,
    val username: String?,
    val photoLink: String?,
    val email: String?,
    val phone: String?,
    val lastLogin: Long = System.currentTimeMillis(),
    val createdTime: Long = System.currentTimeMillis(),
    val isEmailVerified: Boolean = false,
    val isAnonymous: Boolean = false,
    val mfa: MutableList<MultiFactorInfo> = mutableListOf(),
) {
    companion object DATA {
        var uid: String = ""
        var username: String = ""
        var photoLink: String = ""
        var email: String = ""
        var phone: String = ""
        var lastLogin: Long = System.currentTimeMillis()
        var createdTime: Long = System.currentTimeMillis()
        var isEmailVerified: Boolean = false
        var isAnonymous: Boolean = false
        var mfa: MutableList<MultiFactorInfo> = mutableListOf()
        @JvmStatic var offlineMode by mutableStateOf(false)
        fun toggle() = run { offlineMode = !offlineMode }
    }

}
