package com.example.agileandroidalpha.feature_board.presentation.settings

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.feature_board.presentation.users.UserState
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: AuthRepo
) : ViewModel() {
    private val _state = mutableStateOf(UserState())
    val state: State<UserState> = _state
    val user = Firebase.auth.currentUser
    private var fireUser = mutableStateOf(FireUser())
    private val userRef = Firebase.firestore.collection("users")

    init {
        viewModelScope.launch {
            user?.let { u ->
                _state.value = state.value.copy(
                    uid = u.uid,
                    name = u.displayName ?: "",
                    email = u.email ?: "",
                    phone = u.phoneNumber ?: "",
                    photo = u.photoUrl.toString(),
                    emailVerified = u.isEmailVerified,
                    anon = u.isAnonymous,
                    providerId = u.providerId,
                    tid = u.tenantId
                )
                loadUser()?.let { fireUser.value = it }
            }
        }
    }

    suspend fun loadUser(id: String = user?.uid?: "") : FireUser? {
        var fire: FireUser? = null
        try {
            val snapshot = userRef
                .whereEqualTo("firebaseId", id)
                .limit(1)
                .get()
                .await()
            val sb = StringBuilder()
            snapshot.documents.forEach { doc ->
                fire = doc.toObject<FireUser>()
                if(fire != null) fireUser.value = fire as FireUser
                sb.append("$fireUser\n")
            }
            Log.d("user", "Successfully retrieved firestore user $id")
        } catch(e: Exception) {
            Log.e("user", e.localizedMessage?: "Error loading firestore user data")
        }
        return fire
    }

    fun confirmAction(
        mode: Int = 0,
        confirm: (Boolean) -> Unit
    ) {
        when(mode){
            0 -> { confirm.invoke(true)
                resetPassword()
            }
            1 -> { confirm.invoke(true)
                deleteUser()
            }
        }
    }

    suspend fun disableAccount(

    ) {
        val uid = fireUser.value.uid
        val disabled = fireUser.value.isDisabled
        try {
            uid?.let {
                userRef.document(uid)
                    .update("isDisabled", !disabled)
                    .await()
            }?: Log.d("user", "Invalid FireUser: No uid was found.")
            if (!disabled)
                Log.d("user", "Your account has been disabled and user data will be frozen.")
            else
                Log.d("user", " Your account has been reactivated and all user features" +
                 " are once again available to use.")

        } catch (e: Exception) {
            Log.e("user", e.localizedMessage?: "Error disabling user account")
        }
    }


    fun getUser() = fireUser

    fun resetPassword() = repo.resetPassword()

    fun sendEmailVerification() = repo.verifyUserEmail()

    @OptIn(ExperimentalTime::class)
    fun deleteUser() : Boolean {
        if(Duration.convert(
                System.currentTimeMillis() - user!!.metadata!!.lastSignInTimestamp.toDouble(),
                DurationUnit.MILLISECONDS,
                DurationUnit.DAYS) < 31
        ) {
            viewModelScope.launch {
                userRef.document(user.uid).delete().await()
                repo.deleteUser()
                repo.signOut()
            }
            return true
        }
        repo.reAuth()
        return false
    }


}