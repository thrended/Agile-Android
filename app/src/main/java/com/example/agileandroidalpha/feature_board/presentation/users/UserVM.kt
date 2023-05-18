@file:OptIn(ExperimentalTime::class)

package com.example.agileandroidalpha.feature_board.presentation.users

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.Statics
import com.example.agileandroidalpha.firebase.login.UserData
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@HiltViewModel
class UserVM @Inject constructor(
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
                loadUser()?.let {
                    fireUser.value = it
                    Statics.Users.userMap[user] = fireUser.value
                    Statics.Users.usernameMap[user] = fireUser.value.name?:""
                    Statics.Users.uidFireMap[user.uid] = fireUser.value
                    Statics.Users.uidIDMap[user.uid] = fireUser.value.id
                    Statics.Users.uidNameMap[user.uid] = fireUser.value.name
                    Statics.Users.uidUidMap[user.uid] = fireUser.value.firebaseId?: fireUser.value.uid
                    fireUser.value.id?.let { Statics.Users.IDNameMap[fireUser.value.id!!] = fireUser.value.name }
                }
                Statics.Users.uidFireMap[u.uid]?.let{ it.isOnline = true}
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

    suspend fun updateUser(
        data: UserData?,
        onRes: () -> Unit = {}
    ) {
        data?.let {
            update(loadUser(), updateMap(data = data))
            UserData.lastLogin = data.lastLogin
            UserData.createdTime = data.createdTime
            UserData.username = data.username.orEmpty()
            UserData.email = data.email.orEmpty()
            UserData.uid = data.uid
            UserData.photoLink = data.photoLink.orEmpty()
            UserData.phone= data.phone.orEmpty()
            UserData.isAnonymous = data.isAnonymous
            UserData.isEmailVerified = data.isEmailVerified
            UserData.mfa = data.mfa
            onRes.invoke()
        }

    }

    suspend fun update(
        u: FireUser? = fireUser.value, map: Map<String, Any>,
    ) {
        if (map.isNotEmpty()) {
            try {
                val query = userRef
                    .whereEqualTo("id", u?.id)
                    .limit(1)
                    .get()
                    .await()
                if (query.documents.isNotEmpty()) {
                    for (doc in query) {
                        userRef.document(doc.id).set(
                            map,
                            SetOptions.merge()
                        ).await()

                    }

                }
            } catch (e: Exception) {

            }
        }
    }

    fun updateMap(u: FireUser? = fireUser.value, data: UserData?): Map<String, Any> {
        if (data == null || u == null) return emptyMap()
        val map = mutableMapOf<String, Any>()
        if(u.uid != data.uid) {
            map["uid"] = data.uid
            map["firebaseId"] = data.uid
        }
        if(!data.username.isNullOrBlank() && u.name != data.username) {
            map["name"] = data.username
            val sz = data.username.split(" ").size
            map["firstName"] = data.username.split(" ").first()
            map["lastName"] = if (sz > 1) data.username.split(" ").last() else ""
            map["middleName"] = if (sz > 2) data.username.split(" ").subList(1, sz - 1).joinToString( separator = " ")
                                else ""
        }
        if(!data.photoLink.isNullOrBlank() && u.photo != data.photoLink) {
            map["photo"] = data.photoLink
        }
        if(!data.email.isNullOrBlank() && u.email != data.email) {
            map["email"] = data.email
        }
        if(u.isVerified != data.isEmailVerified) {
            map["isVerified"] = data.isEmailVerified
        }
        if(u.reputation > 9 && !u.isPowerUser || (u.reputation < 6 && u.isPowerUser)) {
            map["isPowerUser"] = u.reputation > 5
        }
        if(u.reputation > 30 && !u.isAdmin || (u.reputation < 21 && u.isAdmin)) {
            map["isAdmin"] = u.reputation > 20
        }
        if(u.reputation > 99 && !u.isHeadmaster || (u.reputation < 70 && u.isHeadmaster)) {
            map["isHeadmaster"] = u.reputation > 69
        }
        if(!data.phone.isNullOrBlank() && u.phone != data.phone) {
            map["phone"] = data.phone
        }
        if(data.lastLogin > u.lastLogin) {
            map["lastLogin"] = data.lastLogin
        }
        if( (data.mfa.isNotEmpty() && !u.mfa )
            || (data.mfa.isEmpty() && u.mfa ) ) {
            map["mfa"] = data.mfa.isNotEmpty()
        }
        if (UserData.offlineMode != u.isOfflineMode) {
            map["isOfflineMode"] = UserData.offlineMode
        }
        map["isOnline"] = true

        return map
    }

    fun getUserInfo() {
        user?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            // Check if user's email is verified
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid
            _state.value = state.value.copy(
                name = name ?: "",
                email = email ?: "",
                photo = photoUrl.toString(),
                emailVerified = emailVerified,
                anon = false,
                firebaseId = uid
            )
        }
    }

    fun getUserProviderData(){
        user?.let {
            for (profile in it.providerData) {
                // Id of the provider (ex: google.com)
                val providerId = profile.providerId

                // UID specific to the provider
                val uid = profile.uid

                // Name, email address, and profile photo Url
                val name = profile.displayName
                val email = profile.email
                val photoUrl = profile.photoUrl
            }
        }
    }

    fun change(s: String, mode: Int = 0) {
        when (mode) {
            0 -> {
                _state.value = state.value.copy(
                    name = s
                )
            }
            1 -> {
                _state.value = state.value.copy(
                    photo = s
                )
            }
            2 -> {
                _state.value = state.value.copy(
                    email = s
                )
            }
            else -> {
                _state.value = state.value.copy(
                    phone = s
                )
            }
        }
    }

    fun updateProfile(s: String, p: String, u: String? = null, ph: String? = null) {
        val updates = userProfileChangeRequest {
            displayName = s
            photoUri = Uri.parse(p)
        }
        repo.updateUserProfile(updates)
        user!!.reload()
        u?.let { UserData.email = it }
        ph?.let { UserData.phone = it }
    }

    fun changePassword(pw: String) : Boolean {
        if (pw.length in 6..66) {
            repo.changePassword(pw)
            return true
        }
        return false
    }


}